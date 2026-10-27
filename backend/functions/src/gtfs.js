const DAY=86400000;
function invalid() { const error=new Error('Invalid or unsupported GTFS input');error.code='invalid-feed';throw error; }
function id(value) {
  if(typeof value!=='string' || !value || value.length>256 || /[\x00-\x1f\x7f]/.test(value)) invalid();
  return value;
}
function integer(value,maximum=1000000) {
  if(typeof value!=='string' || !/^\d+$/.test(value) || !Number.isSafeInteger(Number(value)) || Number(value)>maximum) invalid();
  return Number(value);
}
function dateMillis(value) {
  if(typeof value!=='string' || !/^\d{8}$/.test(value)) invalid();
  const iso=`${value.slice(0,4)}-${value.slice(4,6)}-${value.slice(6,8)}`;
  const result=Date.parse(iso+'T00:00:00Z');
  if(!Number.isFinite(result) || new Date(result).toISOString().slice(0,10)!==iso) invalid();
  return result;
}
function serviceSeconds(value) {
  if(typeof value!=='string' || !/^\d{1,2}:[0-5]\d:[0-5]\d$/.test(value)) invalid();
  const [hours,minutes,seconds]=value.split(':').map(Number);
  if(hours>=72) invalid();
  return hours*3600+minutes*60+seconds;
}
function instantMillis(value) {
  if(typeof value!=='string' || !/^\d{4}-\d{2}-\d{2}T(?:[01]\d|2[0-3]):[0-5]\d:[0-5]\d(?:\.\d{1,9})?(?:Z|[+-](?:0\d|1[0-4]):[0-5]\d)$/i.test(value)) invalid();
  dateMillis(value.slice(0,10).replaceAll('-',''));
  const result=Date.parse(value);
  if(!Number.isFinite(result)) invalid();
  return result;
}

/** Bounded offline CSV ingestion, including quoted fields. No network or ZIP I/O. */
function readCsv(input) {
  if(typeof input!=='string' || new TextEncoder().encode(input).length>16*1024*1024) invalid();
  const text=input.replace(/^\uFEFF/,'');
  let headers,fields=[],field='',quoted=false,closed=false;
  const rows=[];
  function value() {
    fields.push(field);field='';closed=false;
    if(fields.length>128) invalid();
  }
  function row() {
    if(fields.length===1 && fields[0]==='') {fields=[];return;}
    if(!headers) {
      headers=fields;
      if(headers.some(name=>!name) || new Set(headers).size!==headers.length) invalid();
    } else {
      if(fields.length!==headers.length || rows.length>=500000) invalid();
      const record=Object.create(null);
      headers.forEach((name,index)=>{record[name]=fields[index];});
      rows.push(record);
    }
    fields=[];
  }
  for(let index=0;index<text.length;index++) {
    const ch=text[index];
    if(quoted) {
      if(ch==='"') {
        if(text[index+1]==='"') {field+='"';index++;}
        else {quoted=false;closed=true;}
      } else field+=ch;
    } else if(ch===',' || ch==='\n' || ch==='\r') {
      value();
      if(ch!==',') {row();if(ch==='\r' && text[index+1]==='\n') index++;}
    } else if(closed) invalid();
    else if(ch==='"') {if(field) invalid();quoted=true;}
    else field+=ch;
    if(field.length>16384) invalid();
  }
  if(quoted) invalid();
  if(field || fields.length || closed) {value();row();}
  return rows;
}

function normalizeGtfs(tables,source) {
  if(!tables || typeof tables!=='object' || !source || !/^[a-z0-9_-]{1,48}$/.test(source.id??'')
      || typeof source.url!=='string') invalid();
  instantMillis(source.fetchedAt);
  let sourceUrl;
  try {sourceUrl=new URL(source.url);} catch {invalid();}
  if(sourceUrl.protocol!=='https:' || !sourceUrl.hostname || sourceUrl.username || sourceUrl.password) invalid();
  const table=(name,required=false)=>{
    if(!Object.hasOwn(tables,name)) {if(required) invalid();return [];}
    const result=readCsv(tables[name]);
    if(required && !result.length) invalid();
    return result;
  };
  const agencies=table('agency.txt',true);
  if(agencies.some(row=>!['Asia/Kuala_Lumpur','Asia/Singapore'].includes(row.agency_timezone))) invalid();
  const information=table('feed_info.txt');
  if(information.length>1) invalid();
  let coverage=null;
  if(information.length && (information[0].feed_start_date || information[0].feed_end_date)) {
    const start=dateMillis(information[0].feed_start_date),end=dateMillis(information[0].feed_end_date);
    if(start>end) invalid();
    coverage={start,end};
  }
  const qualify=value=>`${source.id}:${id(value)}`;
  const stops=new Map(),routes=new Map(),calendars=new Map(),exceptions=new Map();
  for(const row of table('stops.txt',true)) {
    if(row.location_type && row.location_type!=='0') continue;
    const key=qualify(row.stop_id),latitude=Number(row.stop_lat),longitude=Number(row.stop_lon);
    if(stops.has(key) || row.stop_lat==='' || row.stop_lon==='' || !Number.isFinite(latitude)
        || !Number.isFinite(longitude) || Math.abs(latitude)>90 || Math.abs(longitude)>180) invalid();
    stops.set(key,{id:key,name:id(row.stop_name),latitude,longitude});
  }
  if(stops.size>20000) invalid();
  for(const row of table('routes.txt',true)) {
    const key=qualify(row.route_id);
    if(routes.has(key)) invalid();
    routes.set(key,{id:key,name:row.route_short_name||row.route_long_name||row.route_id,type:integer(row.route_type,9999)});
  }
  if(routes.size>2000) invalid();
  const weekdays=['sunday','monday','tuesday','wednesday','thursday','friday','saturday'];
  for(const row of table('calendar.txt')) {
    const key=id(row.service_id),start=dateMillis(row.start_date),end=dateMillis(row.end_date);
    if(calendars.has(key) || start>end || weekdays.some(day=>!['0','1'].includes(row[day]))) invalid();
    calendars.set(key,{start,end,days:weekdays.map(day=>row[day]==='1')});
  }
  for(const row of table('calendar_dates.txt')) {
    const service=id(row.service_id),date=dateMillis(row.date);
    if(!['1','2'].includes(row.exception_type)) invalid();
    if(!exceptions.has(service)) exceptions.set(service,new Map());
    if(exceptions.get(service).has(date)) invalid();
    exceptions.get(service).set(date,row.exception_type==='1');
  }
  if(!calendars.size && !exceptions.size) invalid();
  const rawTrips=new Map(),times=new Map(),frequencies=new Map();
  for(const row of table('trips.txt',true)) {
    const key=id(row.trip_id);
    if(rawTrips.has(key)) invalid();
    rawTrips.set(key,row);
  }
  if(rawTrips.size>50000) invalid();
  for(const row of table('stop_times.txt',true)) {
    const key=id(row.trip_id);
    if(!rawTrips.has(key)) invalid();
    if(!times.has(key)) times.set(key,[]);
    times.get(key).push(row);
  }
  for(const row of table('frequencies.txt')) {
    const key=id(row.trip_id);
    if(!rawTrips.has(key)) invalid();
    if(!frequencies.has(key)) frequencies.set(key,[]);
    frequencies.get(key).push(row);
  }
  const trips=[],issues=[];
  for(const [key,row] of rawTrips) {
    try {
      const routeId=qualify(row.route_id),serviceId=id(row.service_id);
      if(!routes.has(routeId) || (!calendars.has(serviceId) && !exceptions.has(serviceId))) invalid();
      const sequence=(times.get(key)??[]).map(stop=>{
        const stopId=qualify(stop.stop_id);
        if(!stops.has(stopId) || stop.start_pickup_drop_off_window || stop.end_pickup_drop_off_window) invalid();
        for(const name of ['pickup_type','drop_off_type']) if(stop[name] && !['0','1','2','3'].includes(stop[name])) invalid();
        if(stop.timepoint && !['0','1'].includes(stop.timepoint)) invalid();
        return {id:stopId,sequence:integer(stop.stop_sequence),arrival:serviceSeconds(stop.arrival_time),
          departure:serviceSeconds(stop.departure_time),pickup:!stop.pickup_type || stop.pickup_type==='0',
          dropOff:!stop.drop_off_type || stop.drop_off_type==='0',estimated:stop.timepoint==='0'};
      }).sort((a,b)=>a.sequence-b.sequence);
      if(sequence.length<2 || sequence.length>1000) invalid();
      sequence.forEach((stop,index)=>{
        if(stop.arrival>stop.departure || (index && (stop.sequence===sequence[index-1].sequence
            || stop.arrival<sequence[index-1].departure))) invalid();
      });
      const windows=(frequencies.get(key)??[]).map(window=>{
        const start=serviceSeconds(window.start_time),end=serviceSeconds(window.end_time),headway=integer(window.headway_secs,86400);
        if(start>=end || !headway || (window.exact_times && !['0','1'].includes(window.exact_times))) invalid();
        return {start,end,headway,exact:window.exact_times==='1'};
      }).sort((a,b)=>a.start-b.start);
      if(windows.some((window,index)=>index && window.start<windows[index-1].end)) invalid();
      trips.push({id:qualify(key),routeId,serviceId,stops:sequence,frequencies:windows,
        estimated:sequence.some(stop=>stop.estimated)});
    } catch {issues.push({tripId:qualify(key),code:'invalid-trip'});}
  }
  const transferRules=new Map();
  for(const row of table('transfers.txt')) {
    const from=qualify(row.from_stop_id),to=qualify(row.to_stop_id),type=row.transfer_type||'0';
    if(!stops.has(from) || !stops.has(to) || !['0','1','2','3','4','5'].includes(type)) invalid();
    const scoped=['from_trip_id','to_trip_id','from_route_id','to_route_id'].some(name=>Boolean(row[name]));
    const blocked=scoped || !['0','2'].includes(type);
    const minimumSeconds=type==='2'?integer(row.min_transfer_time,21600):0;
    const key=JSON.stringify([from,to]),old=transferRules.get(key);
    transferRules.set(key,{from,to,blocked:blocked||Boolean(old?.blocked),
      minimumSeconds:Math.max(minimumSeconds,old?.minimumSeconds??0)});
  }
  return {source:{id:source.id,url:source.url,fetchedAt:source.fetchedAt},stops,routes,trips,calendars,exceptions,
    transferRules,coverage,fareSen:null,issues};
}

function isServiceActive(feed,serviceId,date) {
  const value=dateMillis(date),override=feed.exceptions.get(serviceId)?.get(value);
  if(override!==undefined) return override;
  const calendar=feed.calendars.get(serviceId);
  return Boolean(calendar && value>=calendar.start && value<=calendar.end
    && calendar.days[new Date(value).getUTCDay()]);
}

module.exports={readCsv,serviceSeconds,normalizeGtfs,isServiceActive,dateMillis,instantMillis,DAY};
