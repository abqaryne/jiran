const {isServiceActive,dateMillis,instantMillis,DAY}=require('./gtfs');
const OFFSET=8*3600;
function invalid() {const error=new Error('Invalid transit query');error.code='invalid-query';throw error;}
function localTime(text) {
  if(typeof text!=='string' || !/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\+08:00$/.test(text)) invalid();
  const date=text.slice(0,10).replaceAll('-','');
  try {dateMillis(date);} catch {invalid();}
  const seconds=Date.parse(text)/1000;
  if(!Number.isSafeInteger(seconds) || new Date((seconds+OFFSET)*1000).toISOString().slice(0,19)!==text.slice(0,19)) invalid();
  return seconds;
}
const bounded=(value,min,max)=>Number.isInteger(value) && value>=min && value<=max;
const unavailable=reason=>({status:'unavailable',reason,fareSen:null,onTime:null,legs:[]});

/** Bounded schedule research. No fare inference, geographic transfer inference or live guarantee. */
function planJourney(feeds,query) {
  if(!Array.isArray(feeds) || !feeds.length || feeds.length>8 || !query || typeof query!=='object') invalid();
  const departure=localTime(query.departTime),deadline=query.classStartTime===undefined?null:localTime(query.classStartTime);
  let asOf;
  try {asOf=instantMillis(query.asOf);} catch {invalid();}
  const maxLegs=query.maxLegs??3,maxDuration=query.maxJourneySeconds??21600;
  const workLimit=query.workLimit??200000,buffer=query.minTransferSeconds;
  if(!bounded(query.maxFeedAgeSeconds,1,604800) || !bounded(buffer,0,3600)
      || !bounded(maxLegs,1,4) || !bounded(maxDuration,1,21600) || !bounded(workLimit,1,200000)) invalid();
  const ids=new Set(),allStops=new Set();
  for(const feed of feeds) {
    if(ids.has(feed.source.id)) invalid();
    ids.add(feed.source.id);
    for(const key of feed.stops.keys()) allStops.add(key);
  }
  if(!allStops.has(query.fromStopId) || !allStops.has(query.toStopId) || query.fromStopId===query.toStopId) invalid();
  const recent=feeds.filter(feed=>asOf>=Date.parse(feed.source.fetchedAt)
    && asOf-Date.parse(feed.source.fetchedAt)<=query.maxFeedAgeSeconds*1000);
  if(!recent.length || !recent.some(feed=>feed.stops.has(query.fromStopId))
      || !recent.some(feed=>feed.stops.has(query.toStopId))) return unavailable('stale-feed');
  const dayZero=dateMillis(query.departTime.slice(0,10).replaceAll('-',''));
  const fresh=recent.filter(feed=>!feed.coverage || (dayZero>=feed.coverage.start && dayZero<=feed.coverage.end));
  if(!fresh.some(feed=>feed.stops.has(query.fromStopId)) || !fresh.some(feed=>feed.stops.has(query.toStopId))) {
    return unavailable('outside-feed-coverage');
  }
  const availableStops=new Set(fresh.flatMap(feed=>[...feed.stops.keys()]));
  const transferLinks=query.transferLinks??[];
  if(!Array.isArray(transferLinks) || transferLinks.length>1000) invalid();
  const links=new Map(),rules=new Map(),boarding=new Map();
  for(const feed of fresh) for(const [key,rule] of feed.transferRules) rules.set(key,rule);
  for(const link of transferLinks) {
    if(!link || !allStops.has(link.fromStopId) || !allStops.has(link.toStopId) || link.fromStopId===link.toStopId
        || !bounded(link.seconds,1,3600) || (link.verified!==undefined && typeof link.verified!=='boolean')) invalid();
    if(rules.get(JSON.stringify([link.fromStopId,link.toStopId]))?.blocked) continue;
    if(!availableStops.has(link.fromStopId) || !availableStops.has(link.toStopId)) continue;
    if(!links.has(link.fromStopId)) links.set(link.fromStopId,[]);
    const minimum=rules.get(JSON.stringify([link.fromStopId,link.toStopId]))?.minimumSeconds??0;
    links.get(link.fromStopId).push({...link,seconds:Math.max(link.seconds,minimum)});
  }
  let work=0,activeTrips=0;
  function tick() {if(++work>workLimit) throw new Error('search-limit');}
  const end=departure+maxDuration;
  try {
    for(const feed of fresh) for(const trip of feed.trips) {
      tick();
      const days=[];
      const first=trip.stops[0].departure,last=trip.stops.at(-1).arrival;
      const earliest=trip.frequencies.length?trip.frequencies.reduce((value,window)=>Math.min(value,window.start),Infinity):first;
      const latest=trip.frequencies.length?trip.frequencies.reduce((value,window)=>Math.max(value,window.end),0)+last-first:last;
      for(let offset=-2;offset<=1;offset++) {
        const day=dayZero+offset*DAY,date=new Date(day).toISOString().slice(0,10).replaceAll('-','');
        const base=day/1000-OFFSET;
        if(base+latest<departure || base+earliest>end) continue;
        if((!feed.coverage || (day>=feed.coverage.start && day<=feed.coverage.end))
            && isServiceActive(feed,trip.serviceId,date)) days.push(base);
      }
      if(!days.length) continue;
      activeTrips++;
      trip.stops.forEach((stop,index)=>{
        tick();
        if(!stop.pickup || index===trip.stops.length-1) return;
        if(!boarding.has(stop.id)) boarding.set(stop.id,[]);
        boarding.get(stop.id).push({trip,index,days,coverage:feed.coverage});
      });
    }
    if(!activeTrips) return unavailable('no-active-service');
    const queue=[],best=new Map();
    let labelCount=0;
    const stateKey=state=>JSON.stringify([state.stop,state.rides,state.estimated,state.alightStop]);
    const dominates=(a,b)=>a.at<=b.at && (a.alightAt??-Infinity)<=(b.alightAt??-Infinity);
    function offer(state) {
      tick();
      if(state.at>end) return;
      const key=stateKey(state),previous=best.get(key)??[];
      if(previous.some(label=>dominates(label,state))) return;
      const retained=previous.filter(label=>!dominates(state,label));
      labelCount+=1+retained.length-previous.length;
      if(labelCount>10000) throw new Error('search-limit');
      best.set(key,[...retained,state]);queue.push(state);
    }
    offer({stop:query.fromStopId,at:departure,rides:0,estimated:false,legs:[],warnings:[]});
    while(queue.length) {
      tick();
      queue.sort((a,b)=>b.at-a.at || b.rides-a.rides || Number(b.estimated)-Number(a.estimated));
      const state=queue.pop();
      if(!best.get(stateKey(state)).includes(state)) continue;
      if(state.stop===query.toStopId) {
        return {status:state.estimated?'estimated':'scheduled',dataSource:'gtfs-schedule',estimated:state.estimated,
          departureTime:new Date(departure*1000).toISOString(),arrivalTime:new Date(state.at*1000).toISOString(),
          durationSeconds:state.at-departure,fareSen:null,onTime:deadline===null || state.estimated?null:state.at<=deadline,
          legs:state.legs,warnings:[...new Set(['fare-unavailable','schedule-not-live',...state.warnings])],
          sources:fresh.map(feed=>({...feed.source}))};
      }
      for(const link of links.get(state.stop)??[]) {
        offer({stop:link.toStopId,at:state.at+link.seconds,rides:state.rides,
          alightStop:state.alightStop,alightAt:state.alightAt,
          estimated:state.estimated||!link.verified,
          warnings:[...state.warnings,...(link.verified?[]:['unverified-transfer'])],
          legs:[...state.legs,{type:'transfer',fromStopId:state.stop,toStopId:link.toStopId,durationSeconds:link.seconds}]});
      }
      if(state.rides>=maxLegs) continue;
      const transferRule=rules.get(JSON.stringify([state.alightStop,state.stop]));
      if(state.rides && transferRule?.blocked) continue;
      const ready=state.rides?Math.max(state.at+buffer,state.alightAt+(transferRule?.minimumSeconds??0)):state.at;
      for(const {trip,index,days,coverage} of boarding.get(state.stop)??[]) {
        const board=trip.stops[index],first=trip.stops[0].departure;
        for(const base of days) {
          tick();
          const runs=[];
          if(!trip.frequencies.length) runs.push({shift:0,estimated:false});
          for(const window of trip.frequencies) {
            tick();
            const earliest=ready-base-(board.departure-first);
            const start=window.exact?window.start+Math.max(0,Math.ceil((earliest-window.start)/window.headway))*window.headway
              :Math.max(window.start,earliest)+window.headway;
            if(start<window.end) runs.push({shift:start-first,estimated:!window.exact});
          }
          for(const run of runs) {
            const boardAt=base+board.departure+run.shift;
            if(boardAt<ready || boardAt>end) continue;
            for(let next=index+1;next<trip.stops.length;next++) {
              tick();
              const target=trip.stops[next];
              if(!target.dropOff) continue;
              const at=base+target.arrival+run.shift;
              if(coverage && at>(coverage.end+DAY)/1000-OFFSET-1) continue;
              const estimated=state.estimated||run.estimated||trip.estimated;
              offer({stop:target.id,at,rides:state.rides+1,estimated,
                alightStop:target.id,alightAt:at,
                warnings:[...state.warnings,...(run.estimated?['frequency-based-service']:[]),
                  ...(trip.estimated?['estimated-stop-times']:[])],
                legs:[...state.legs,{type:'transit',tripId:trip.id,routeId:trip.routeId,
                  fromStopId:state.stop,toStopId:target.id,departureTime:new Date(boardAt*1000).toISOString(),
                  arrivalTime:new Date(at*1000).toISOString(),waitSeconds:boardAt-state.at}]});
            }
          }
        }
      }
    }
    return unavailable('no-scheduled-journey');
  } catch(error) {if(error.message==='search-limit') return unavailable('search-limit');throw error;}
}
module.exports={planJourney};
