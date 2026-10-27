import {build} from 'esbuild';

await build({entryPoints:['src/worker.mjs'],bundle:true,format:'esm',platform:'browser',
  target:'es2022',outfile:'dist/worker.mjs',metafile:true}).then(async result=>{
  const {writeFile,mkdir}=await import('node:fs/promises');
  await mkdir('dist',{recursive:true});
  await writeFile('dist/metafile.json',JSON.stringify(result.metafile,null,2));
});
