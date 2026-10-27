const {normalizeGtfs}=require('../src/gtfs');
const {planJourney}=require('../src/gtfs-journey');
const {tables,source,csv}=require('./fixtures/gtfs');
const query={fromStopId:'test:a',toStopId:'test:b',departTime:'2026-10-27T07:55:00+08:00',
  asOf:'2026-10-27T00:00:00Z',maxFeedAgeSeconds:86400,minTransferSeconds:120};
const plan=(data=tables(),overrides={})=>planJourney([normalizeGtfs(data,source)],{...query,...overrides});

test('direct scheduled service preserves waiting, times and unknown fare',()=>{
  const result=plan();
  expect(result).toMatchObject({status:'scheduled',durationSeconds:1500,fareSen:null,estimated:false,
    arrivalTime:'2026-10-27T00:20:00.000Z'});
  expect(result.legs).toHaveLength(1);
  expect(result.legs[0]).toMatchObject({tripId:'test:t',fromStopId:'test:a',toStopId:'test:b',waitSeconds:300});
  expect(result.warnings).toContain('fare-unavailable');
});
test('cannot board a departed trip, reverse stop order or no-pickup stop',()=>{
  expect(plan(tables(),{departTime:'2026-10-27T08:00:01+08:00'}).status).toBe('unavailable');
  expect(plan(tables(),{fromStopId:'test:b',toStopId:'test:a'}).status).toBe('unavailable');
  const data=tables();
  data['stop_times.txt']=data['stop_times.txt'].replace('departure_time','departure_time,pickup_type')
    .replace('08:00:00,08:00:00','08:00:00,08:00:00,1').replace('08:20:00,08:20:00','08:20:00,08:20:00,0');
  expect(plan(data).status).toBe('unavailable');
});
test('frequency exact grid respects the downstream offset and exclusive end',()=>{
  const data=tables();
  data['frequencies.txt']='trip_id,start_time,end_time,headway_secs,exact_times\nt,08:00:00,09:00:00,600,1';
  expect(plan(data,{departTime:'2026-10-27T08:00:01+08:00'}).arrivalTime).toBe('2026-10-27T00:30:00.000Z');
  expect(plan(data,{departTime:'2026-10-27T09:00:00+08:00'}).status).toBe('unavailable');
});
test('non-exact headways and timepoint estimates cannot certify on-time eligibility',()=>{
  const data=tables();
  data['frequencies.txt']='trip_id,start_time,end_time,headway_secs\nt,08:00:00,09:00:00,600';
  const result=plan(data,{classStartTime:'2026-10-27T09:00:00+08:00'});
  expect(result.status).toBe('estimated');expect(result.onTime).toBeNull();
  expect(result.warnings).toContain('frequency-based-service');
});
test('downstream frequency boarding can occur after the first-stop end time',()=>{
  const data=tables();data['stops.txt']+='\nc,Third,3.3,101.8';
  data['stop_times.txt']+='\nt,c,3,08:40:00,08:40:00';
  data['frequencies.txt']='trip_id,start_time,end_time,headway_secs,exact_times\nt,08:00:00,09:00:00,600,1';
  const result=plan(data,{fromStopId:'test:b',toStopId:'test:c',departTime:'2026-10-27T09:05:00+08:00'});
  expect(result.arrivalTime).toBe('2026-10-27T01:30:00.000Z');
  expect(plan(data,{fromStopId:'test:b',toStopId:'test:c',departTime:'2026-10-27T09:20:00+08:00'}).status).toBe('unavailable');
});
test('scheduled arrival can be compared to a chosen class deadline without creating a fare',()=>{
  expect(plan(tables(),{classStartTime:'2026-10-27T08:20:00+08:00'}).onTime).toBe(true);
  expect(plan(tables(),{classStartTime:'2026-10-27T08:19:59+08:00'}).onTime).toBe(false);
});
test('prior service days after24h and next-day first trips are both considered',()=>{
  const late=tables(); late['stop_times.txt']=late['stop_times.txt'].replaceAll('08:00:00','25:00:00').replaceAll('08:20:00','25:20:00');
  expect(plan(late,{departTime:'2026-10-28T00:55:00+08:00'}).arrivalTime).toBe('2026-10-27T17:20:00.000Z');
  const early=tables(); early['stop_times.txt']=early['stop_times.txt'].replaceAll('08:00:00','00:10:00').replaceAll('08:20:00','00:30:00');
  expect(plan(early,{departTime:'2026-10-27T23:55:00+08:00'}).arrivalTime).toBe('2026-10-27T16:30:00.000Z');
});
test('expired service, removed dates and stale/future snapshots cannot generate journeys',()=>{
  expect(plan(tables(),{departTime:'2026-11-16T07:55:00+08:00'}).reason).toBe('no-active-service');
  const data=tables();data['calendar_dates.txt']='service_id,date,exception_type\ns,20261027,2';
  expect(plan(data).status).toBe('unavailable');
  expect(plan(tables(),{asOf:'2026-10-29T00:00:00Z'}).reason).toBe('stale-feed');
  expect(plan(tables(),{asOf:'2026-10-26T00:00:00Z'}).reason).toBe('stale-feed');
});
function transferTables() {
  const data=tables();
  data['stops.txt']+='\nc,Third,3.3,101.8';
  data['trips.txt']+='\nr,s,second';
  data['stop_times.txt']+='\nsecond,b,1,08:22:00,08:22:00\nsecond,c,2,08:40:00,08:40:00';
  return data;
}
test('same-stop transfer obeys an explicit buffer and maximum boarding count',()=>{
  const data=transferTables();
  expect(plan(data,{toStopId:'test:c'}).legs).toHaveLength(2);
  expect(plan(data,{toStopId:'test:c',minTransferSeconds:121}).status).toBe('unavailable');
  expect(plan(data,{toStopId:'test:c',maxLegs:1}).status).toBe('unavailable');
});
test('nearby differently named stops never create implicit transfers',()=>{
  const one=normalizeGtfs(tables(),source),two=normalizeGtfs(tables(),{...source,id:'other'});
  expect(planJourney([one,two],{...query,toStopId:'other:b'}).status).toBe('unavailable');
  const linked=planJourney([one,two],{...query,toStopId:'other:b',
    transferLinks:[{fromStopId:'test:a',toStopId:'other:a',seconds:60,verified:false}]});
  expect(linked.status).toBe('estimated');expect(linked.onTime).toBeNull();
  expect(linked.warnings).toContain('unverified-transfer');
});
test('GTFS prohibited/minimum and unsupported trip-specific transfers are conservative',()=>{
  const data=transferTables();
  data['transfers.txt']='from_stop_id,to_stop_id,transfer_type,min_transfer_time\nb,b,3,';
  expect(plan(data,{toStopId:'test:c'}).status).toBe('unavailable');
  data['transfers.txt']=data['transfers.txt'].replace('b,b,3,','b,b,2,121');
  expect(plan(data,{toStopId:'test:c'}).status).toBe('unavailable');
  data['transfers.txt']='from_stop_id,to_stop_id,transfer_type,from_trip_id\nb,b,0,t';
  expect(plan(data,{toStopId:'test:c'}).status).toBe('unavailable');
});
test('search limits fail explicitly and source namespaces cannot collide',()=>{
  expect(plan(tables(),{workLimit:1}).reason).toBe('search-limit');
  const feed=normalizeGtfs(tables(),source);
  expect(()=>planJourney([feed,feed],query)).toThrow();
});
test.each(['3,','2,600'])('chained walking links cannot bypass original alighting transfer rule %s',rule=>{
  const data=transferTables();
  data['stops.txt']+='\nx,Interchange,3.25,101.75\nd,Destination,3.4,101.9';
  data['stop_times.txt']=data['stop_times.txt'].replace('second,b,1','second,c,1').replace('second,c,2','second,d,2');
  data['transfers.txt']='from_stop_id,to_stop_id,transfer_type,min_transfer_time\nb,c,'+rule;
  expect(plan(data,{toStopId:'test:d',minTransferSeconds:0,transferLinks:[
    {fromStopId:'test:b',toStopId:'test:x',seconds:30,verified:true},
    {fromStopId:'test:x',toStopId:'test:c',seconds:30,verified:true},
  ]}).status).toBe('unavailable');
});
test('fresh download outside feed_info validity cannot certify a scheduled journey',()=>{
  const data=tables();
  data['feed_info.txt']='feed_start_date,feed_end_date\n20261001,20261026';
  expect(plan(data).reason).toBe('outside-feed-coverage');
});
test('minimum transfer duration permits boarding at its exact boundary after chained walks',()=>{
  const data=transferTables();
  data['stops.txt']+='\nx,Interchange,3.25,101.75\nd,Destination,3.4,101.9';
  data['stop_times.txt']=data['stop_times.txt'].replace('second,b,1','second,c,1')
    .replace('second,c,2','second,d,2').replaceAll('08:22:00','08:30:00');
  data['transfers.txt']='from_stop_id,to_stop_id,transfer_type,min_transfer_time\nb,c,2,600';
  const result=plan(data,{toStopId:'test:d',minTransferSeconds:0,transferLinks:[
    {fromStopId:'test:b',toStopId:'test:x',seconds:30,verified:true},
    {fromStopId:'test:x',toStopId:'test:c',seconds:30,verified:true},
  ]});
  expect(result.status).toBe('scheduled');expect(result.arrivalTime).toBe('2026-10-27T00:40:00.000Z');
});
test('later equivalent alightings do not exhaust a bounded search on a small dense feed',()=>{
  const data=tables();data['stops.txt']+='\nc,Unconnected,3.3,101.8';
  const tripRows=[],timeRows=[];
  for(let index=0;index<25;index++) {
    const minute=String(index).padStart(2,'0');
    tripRows.push(['r','s','t'+index]);
    timeRows.push(['t'+index,'a',1,`08:${minute}:00`,`08:${minute}:00`],
      ['t'+index,'b',2,`09:${minute}:00`,`09:${minute}:00`]);
  }
  tripRows.push(['r','s','return']);
  timeRows.push(['return','b',1,'10:00:00','10:00:00'],['return','a',2,'10:10:00','10:10:00']);
  data['trips.txt']=csv(['route_id','service_id','trip_id'],tripRows);
  data['stop_times.txt']=csv(['trip_id','stop_id','stop_sequence','arrival_time','departure_time'],timeRows);
  expect(plan(data,{toStopId:'test:c',workLimit:400}).reason).toBe('no-scheduled-journey');
});
test('previous-day trips that have already ended do not exhaust boarding-index work',()=>{
  const data=tables();
  data['calendar.txt']+='\nold,1,0,0,0,0,0,0,20261026,20261026';
  for(let index=0;index<80;index++) {
    data['trips.txt']+=`\nr,old,old${index}`;
    data['stop_times.txt']+=`\nold${index},a,1,08:00:00,08:00:00\nold${index},b,2,08:20:00,08:20:00`;
  }
  expect(plan(data,{workLimit:200}).status).toBe('scheduled');
});
test.each([{minTransferSeconds:-1},{maxFeedAgeSeconds:0},{maxLegs:10},{departTime:'2026-02-30T08:00:00+08:00'},
  {asOf:'2026-02-30T00:00:00Z'},
  {departTime:'2026-10-27T08:00:00Z'},{fromStopId:'missing'},{maxJourneySeconds:999999}])('rejects invalid query %j',overrides=>{
  expect(()=>plan(tables(),overrides)).toThrow();
});
