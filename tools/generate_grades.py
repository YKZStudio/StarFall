"""Expand base assets into distinct, exchange-safe graded items."""
import json,pathlib,runpy
base=runpy.run_path(str(pathlib.Path(__file__).with_name('generate_assets.py')))
root=base['ROOT'];write=base['write'];entries=base['entries'];prices=base['prices']
zh=json.loads((root/'assets/starfall/lang/zh_cn.json').read_text());en=json.loads((root/'assets/starfall/lang/en_us.json').read_text())
zh.update({'grade.starfall.basic':'基础 · I','grade.starfall.refined':'精制 · II','grade.starfall.master':'大师 · III'})
en.update({'grade.starfall.basic':'Basic · I','grade.starfall.refined':'Refined · II','grade.starfall.master':'Masterwork · III'})
zh.update({'grade.starfall.heavy':'重装 · IV','grade.starfall.siege':'攻城 · V','grade.starfall.cataclysm':'灾变 · VI'})
en.update({'grade.starfall.heavy':'Heavy · IV','grade.starfall.siege':'Siege · V','grade.starfall.cataclysm':'Cataclysm · VI'})
upgrades=[
 (1,'refined_','精制·','Refined ',{'gunpowder':2,'copper_ingot':2},1.35,1.15,1.5),
 (2,'master_','大师·','Masterwork ',{'gunpowder':2,'amethyst_shard':2,'gold_nugget':1},1.65,1.3,2),
 (3,'heavy_','重装·','Heavy ',{'tnt':2,'iron_block':2,'diamond':1},4,1.8,2),
 (4,'siege_','攻城·','Siege ',{'tnt':2,'diamond_block':2,'gold_block':1},12,2.7,2),
 (5,'cataclysm_','灾变·','Cataclysm ',{'tnt':2,'netherite_ingot':2,'diamond_block':1},40,4,2)]
explosives={'blast_rocket','safe_rocket','blast_arrow'}
all_arrows=[];rows=[]
for id,cn,name,color,ings,count,tip,engtip in entries:
 prev=id;prevvalue=sum(prices[k]*v for k,v in ings.items())//count
 rows.append({'id':id,'value':prevvalue,'ingredients':ings,'count':count,'grade':0})
 if id.endswith('arrow'):all_arrows.append('starfall:'+id)
 for grade,prefix,cnpre,enpre,extra,damage,radius,duration in upgrades:
  if grade>2 and id not in explosives:continue
  new=prefix+id
  # Four arrows per upgrade; one rocket per upgrade. At most nine crafting slots.
  ingredients=['starfall:'+prev]*count+[f'minecraft:{k}' for k,v in extra.items() for _ in range(v)]
  assert len(ingredients)<=9
  write(f'data/starfall/recipe/{new}.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':ingredients,'result':{'id':'starfall:'+new,'count':count}})
  write(f'data/starfall/advancement/recipes/{new}.json',{'parent':'minecraft:recipes/root','criteria':{'has_previous':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':'starfall:'+prev}]}},'has_the_recipe':{'trigger':'minecraft:recipe_unlocked','conditions':{'recipe':'starfall:'+new}}},'requirements':[['has_previous','has_the_recipe']],'rewards':{'recipes':['starfall:'+new]}})
  value=(prevvalue*count+sum(prices[k]*v for k,v in extra.items()))//count
  write(f'data/starfall/energyexchange/values/{new}.json',{'value':str(value)})
  model=json.loads((root/f'assets/starfall/models/item/{id}.json').read_text())
  def box(a,b):return {'from':a,'to':b,'faces':{f:{'texture':'#all','uv':[8,0,9,1]} for f in ['north','south','east','west','up','down']}}
  # Gold collars distinguish all grades; high-payload heads also get armor bands.
  for i in range(grade):
   y=5.6+i*1.15
   model['elements'].append(box([5.65,y,5.65] if id.endswith('rocket') else [7.1,y,7.1],[10.35,y+.4,10.35] if id.endswith('rocket') else [8.9,y+.4,8.9]))
  if grade>2:
   for i in range(grade-2):
    y=11+i*.85
    model['elements'].append(box([5.6,y,5.6] if id.endswith('rocket') else [6.1,y,6.1],[10.4,y+.45,10.4] if id.endswith('rocket') else [9.9,y+.45,9.9]))
  write(f'assets/starfall/models/item/{new}.json',model)
  write(f'assets/starfall/items/{new}.json',{'model':{'type':'minecraft:model','model':'starfall:item/'+new}})
  zh['item.starfall.'+new]=cnpre+cn;en['item.starfall.'+new]=enpre+name
  if id in explosives:
   cap=(6 if id=='blast_arrow' else 10)*damage
   zh['tip.starfall.'+new]=f'爆炸伤害上限 {cap:g} 点；范围 ×{radius:g}。'+('可破坏地形。' if id=='blast_rocket' else '始终不破坏地形。')
   en['tip.starfall.'+new]=f'Explosion cap {cap:g} HP; radius x{radius:g}. '+('May break terrain.' if id=='blast_rocket' else 'Never breaks terrain.')
  elif id.endswith('arrow'):
   desc={'frost_arrow':f'缓慢 II，持续 {3*duration:g} 秒','pulse_arrow':'更大范围击退','mark_arrow':f'发光 {8*duration:g} 秒','venom_arrow':f'中毒 I，持续 {3*duration:g} 秒','gravity_arrow':'水平牵引至落点'}[id]
   zh['tip.starfall.'+new]=f'直击 {4*damage:g} 点；{desc}；范围 ×{radius:g}。'
   effect={'frost_arrow':f'Slowness II for {3*duration:g}s','pulse_arrow':'radial knockback','mark_arrow':f'Glowing for {8*duration:g}s','venom_arrow':f'Poison I for {3*duration:g}s','gravity_arrow':'horizontal pull toward impact'}[id]
   en['tip.starfall.'+new]=f'Direct hit {4*damage:g} HP; {effect}; radius x{radius:g}.'
  else:
   zh['tip.starfall.'+new]=f'无伤害；绽放尺寸与粒子数量 ×{radius:g}。'
   en['tip.starfall.'+new]=f'Harmless; burst size and particle count x{radius:g}.'
  if id.endswith('arrow'):all_arrows.append('starfall:'+new)
  rows.append({'id':new,'value':value,'ingredients':ingredients,'count':count,'grade':grade,'previous':prev})
  prev=new;prevvalue=value
write('assets/starfall/lang/zh_cn.json',zh);write('assets/starfall/lang/en_us.json',en)
write('data/minecraft/tags/item/arrows.json',{'replace':False,'values':all_arrows})
p=pathlib.Path(__file__).resolve().parents[1]/'docs';p.mkdir(exist_ok=True);(p/'balance.json').write_text(json.dumps(rows,ensure_ascii=False,indent=2)+'\n')
print(f'Expanded to {len(rows)} items with bounded upgrades and conservative conversion values.')
