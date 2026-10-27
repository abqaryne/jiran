const source={id:'test',url:'https://example.test/feed',fetchedAt:'2026-10-27T00:00:00Z'};
const csv=(headers,rows)=>[headers.join(','),...rows.map(row=>row.join(','))].join('\n');
function tables() {
  return {
    'agency.txt':'agency_timezone\nAsia/Kuala_Lumpur',
    'stops.txt':csv(['stop_id','stop_name','stop_lat','stop_lon'],[['a','Start','3.1','101.6'],['b','Finish','3.2','101.7']]),
    'routes.txt':'route_id,route_short_name,route_type\nr,Bus,3',
    'trips.txt':'route_id,service_id,trip_id\nr,s,t',
    'stop_times.txt':csv(['trip_id','stop_id','stop_sequence','arrival_time','departure_time'],
      [['t','a','1','08:00:00','08:00:00'],['t','b','2','08:20:00','08:20:00']]),
    'calendar.txt':'service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date\ns,1,1,1,1,1,0,0,20261001,20261113',
  };
}
module.exports={tables,source,csv};
