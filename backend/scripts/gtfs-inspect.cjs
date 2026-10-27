// Offline public-feed research only; never fetches or writes a production dataset.
const fs=require('node:fs');
const path=require('node:path');
const crypto=require('node:crypto');
const {normalizeGtfs}=require('../functions/src/gtfs');
const {planJourney}=require('../functions/src/gtfs-journey');
const files=['agency.txt','stops.txt','routes.txt','trips.txt','stop_times.txt',
  'calendar.txt','calendar_dates.txt','frequencies.txt','transfers.txt','feed_info.txt'];
function inspect(spec,baseDirectory) {
  if(!spec || !Array.isArray(spec.feeds) || !spec.feeds.length || spec.feeds.length>8
      || !Array.isArray(spec.queries) || spec.queries.length>20) throw new Error('Invalid inspection specification');
  const summaries=[],feeds=spec.feeds.map(entry=>{
    const directory=path.resolve(baseDirectory,entry.directory),tables={},hashes={};
    for(const name of files) {
      const file=path.join(directory,name);
      if(!fs.existsSync(file)) continue;
      const stat=fs.statSync(file);
      if(!stat.isFile() || stat.size>16*1024*1024) throw new Error('GTFS file exceeds research bounds');
      const bytes=fs.readFileSync(file);
      tables[name]=new TextDecoder('utf-8',{fatal:true}).decode(bytes);
      hashes[name]=crypto.createHash('sha256').update(bytes).digest('hex');
    }
    const feed=normalizeGtfs(tables,entry.source);
    summaries.push({source:feed.source,stops:feed.stops.size,routes:feed.routes.size,
      trips:feed.trips.length,excludedTrips:feed.issues.length,issues:feed.issues.slice(0,20),hashes});
    return feed;
  });
  return {researchOnly:true,feeds:summaries,probes:spec.queries.map(query=>({
    fromStopId:query.fromStopId,toStopId:query.toStopId,departTime:query.departTime,
    result:planJourney(feeds,query),
  }))};
}
if(require.main===module) {
  try {
    if(process.argv.length!==3) throw new Error('Usage: node scripts/gtfs-inspect.cjs <public-feed-spec.json>');
    const file=path.resolve(process.argv[2]);
    if(fs.statSync(file).size>65536) throw new Error('Inspection specification too large');
    process.stdout.write(JSON.stringify(inspect(JSON.parse(fs.readFileSync(file,'utf8')),path.dirname(file)),null,2)+'\n');
  } catch(error) {console.error(error.message);process.exitCode=1;}
}
module.exports={inspect};
