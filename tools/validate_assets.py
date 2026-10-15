"""Validate IDs, resources, grade recipes and EnergyExchange anti-arbitrage."""
import json,pathlib
r=pathlib.Path(__file__).resolve().parents[1];res=r/'src/main/resources'
rows=json.loads((r/'docs/balance.json').read_text());values={'minecraft:'+k:v for k,v in {'arrow':33,'firework_rocket':64,'gunpowder':192,'iron_ingot':252,'copper_ingot':8,'amethyst_shard':64,'snowball':64,'glowstone_dust':64,'slime_ball':8,'gold_nugget':227,'redstone':64}.items()}
assert len(rows)==27
for row in rows:values['starfall:'+row['id']]=row['value']
for row in rows:
 id=row['id'];recipe=json.loads((res/f'data/starfall/recipe/{id}.json').read_text())
 cost=sum(values[x] for x in recipe['ingredients'])
 assert row['value']*recipe['result']['count']<=cost,(id,'crafting energy arbitrage')
 assert 1<=len(recipe['ingredients'])<=9
 price=json.loads((res/f'data/starfall/energyexchange/values/{id}.json').read_text());assert price=={'value':str(row['value'])}
 item=json.loads((res/f'assets/starfall/items/{id}.json').read_text());assert item['model']['model']=='starfall:item/'+id
 model=json.loads((res/f'assets/starfall/models/item/{id}.json').read_text());assert len(model['elements'])>=8
 for tex in model['textures'].values():
  assert (res/('assets/'+tex.replace(':','/textures/')+'.png')).exists(),tex
 for box in model['elements']:
  assert all(-16<=a<=b<=32 for a,b in zip(box['from'],box['to']))
 if row['grade']>0:
  base=id.removeprefix('master_').removeprefix('refined_');prev=base if row['grade']==1 else 'refined_'+base
  assert row['value']>values['starfall:'+prev]
zh=json.loads((res/'assets/starfall/lang/zh_cn.json').read_text());en=json.loads((res/'assets/starfall/lang/en_us.json').read_text())
assert zh.keys()==en.keys()
for key in zh:assert zh[key].count('%s')==en[key].count('%s')
for row in rows:
 assert 'item.starfall.'+row['id'] in zh
 assert 'tip.starfall.'+row['id'] in zh
print('PASS: 27 item/model/recipe/value chains; 18 monotonic upgrades; no recipe energy profit; complete bilingual keys.')
