const {readCsv,serviceSeconds,normalizeGtfs,isServiceActive}=require('../src/gtfs');

const {tables,source}=require('./fixtures/gtfs');
const normalize=data=>normalizeGtfs(data??tables(),source);

test('CSV supports UTF8 BOM, CRLF, commas, quoted newlines and escaped quotes',()=>{
  expect(readCsv('\uFEFFid,name\r\na,"Room, \"\"Blue\"\"\nUM"\r\n')).toEqual([{id:'a',name:'Room, "Blue"\nUM'}]);
});
test.each(['id,id\na,b','id,name\na','id\n"unfinished','id\n"a"junk','id\na"b'])('rejects malformed CSV %s',text=>{
  expect(()=>readCsv(text)).toThrow();
});
test('CSV handles prototype-shaped headers as ordinary fields and bounds input',()=>{
  const [row]=readCsv('__proto__,constructor\nvalue,other');
  expect(Object.hasOwn(row,'__proto__')).toBe(true);
  expect(row.__proto__).toBe('value');
  expect(()=>readCsv('id\n'+'x'.repeat(16385))).toThrow();
});
test.each([['8:05:09',29109],['25:00:00',90000],['71:59:59',259199],['00:00:00',0]])(
  'service time %s preserves extended-day seconds', (text,expected)=>expect(serviceSeconds(text)).toBe(expected));
test.each(['24:60:00','08:00','-1:00:00','72:00:00','','8:5:00'])('rejects unsupported service time %s',text=>{
  expect(()=>serviceSeconds(text)).toThrow();
});
test('normalization namespaces IDs and sorts stop sequence without inferring fares',()=>{
  const data=tables();
  data['stop_times.txt']=data['stop_times.txt'].split('\n').map((row,i,array)=>i===1?array[2]:i===2?array[1]:row).join('\n');
  const feed=normalize(data);
  expect(feed.source).toEqual(source);
  expect(feed.stops.get('test:a')).toMatchObject({name:'Start',latitude:3.1,longitude:101.6});
  expect(feed.trips[0]).toMatchObject({id:'test:t',routeId:'test:r',serviceId:'s',estimated:false});
  expect(feed.trips[0].stops.map(stop=>stop.id)).toEqual(['test:a','test:b']);
  expect(feed.fareSen).toBeNull();
});
test('calendar weekdays, boundaries and removal/addition exceptions are explicit',()=>{
  const data=tables();
  data['calendar_dates.txt']='service_id,date,exception_type\ns,20261109,2\ns,20261114,1';
  const feed=normalize(data);
  expect(isServiceActive(feed,'s','20261109')).toBe(false);
  expect(isServiceActive(feed,'s','20261113')).toBe(true);
  expect(isServiceActive(feed,'s','20261114')).toBe(true);
  expect(isServiceActive(feed,'s','20261116')).toBe(false);
  expect(isServiceActive(feed,'unknown','20261113')).toBe(false);
});
test('calendar_dates alone can define service; invalid dates and duplicate overrides fail',()=>{
  const data=tables(); delete data['calendar.txt'];
  data['calendar_dates.txt']='service_id,date,exception_type\ns,20261116,1';
  expect(isServiceActive(normalize(data),'s','20261116')).toBe(true);
  expect(()=>isServiceActive(normalize(data),'s','20260230')).toThrow();
  data['calendar_dates.txt']+='\ns,20261116,2';
  expect(()=>normalize(data)).toThrow();
});
test('feed validity dates must be complete, ordered and real calendar dates',()=>{
  for(const row of ['20261027,','20261027,20261026','20260230,20260301']) {
    const data=tables();data['feed_info.txt']='feed_start_date,feed_end_date\n'+row;
    expect(()=>normalize(data)).toThrow();
  }
});
test('headway windows default to estimated and may touch but not overlap',()=>{
  const data=tables();
  data['frequencies.txt']='trip_id,start_time,end_time,headway_secs,exact_times\nt,08:00:00,09:00:00,600,\nt,09:00:00,10:00:00,300,1';
  expect(normalize(data).trips[0].frequencies).toEqual([
    {start:28800,end:32400,headway:600,exact:false},{start:32400,end:36000,headway:300,exact:true},
  ]);
  data['frequencies.txt']=data['frequencies.txt'].replace('09:00:00,10:00:00','08:59:00,10:00:00');
  expect(normalize(data).trips).toHaveLength(0);
  expect(normalize(data).issues[0].code).toBe('invalid-trip');
});
test.each([
  text=>text.replace('08:20:00,08:20:00','07:59:00,07:59:00'),
  text=>text.replace('b,2','b,1'),
  text=>text.replace('08:20:00,08:20:00',','),
  text=>text.replace('b,2','missing,2'),
])('invalid trip data is quarantined without a scheduled fallback',change=>{
  const data=tables(); data['stop_times.txt']=change(data['stop_times.txt']);
  const feed=normalize(data);
  expect(feed.trips).toEqual([]); expect(feed.issues).toHaveLength(1);
});
test('pickup/drop-off restrictions and estimated timepoints survive normalization',()=>{
  const data=tables();
  data['stop_times.txt']='trip_id,stop_id,stop_sequence,arrival_time,departure_time,pickup_type,drop_off_type,timepoint\nt,a,1,08:00:00,08:00:00,1,0,1\nt,b,2,08:20:00,08:20:00,0,2,0';
  const trip=normalize(data).trips[0];
  expect(trip.estimated).toBe(true);
  expect(trip.stops[0].pickup).toBe(false);
  expect(trip.stops[1].dropOff).toBe(false);
});
test.each(['agency.txt','stops.txt','routes.txt','trips.txt','stop_times.txt'])('missing %s is rejected',name=>{
  const data=tables(); delete data[name]; expect(()=>normalize(data)).toThrow();
});
test('unsupported time zone, duplicate stop IDs, missing calendars and invalid provenance fail closed',()=>{
  const zone=tables();zone['agency.txt']='agency_timezone\nEurope/London';expect(()=>normalize(zone)).toThrow();
  const dup=tables();dup['stops.txt']+='\na,Duplicate,3.1,101.6';expect(()=>normalize(dup)).toThrow();
  const calendar=tables();delete calendar['calendar.txt'];expect(()=>normalize(calendar)).toThrow();
  expect(()=>normalizeGtfs(tables(),{...source,id:'bad:namespace'})).toThrow();
  expect(()=>normalizeGtfs(tables(),{...source,fetchedAt:'not-a-date'})).toThrow();
  expect(()=>normalizeGtfs(tables(),{...source,fetchedAt:'2026-02-30T00:00:00Z'})).toThrow();
  expect(()=>normalizeGtfs(tables(),{...source,url:'https://'})).toThrow();
});

