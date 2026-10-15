"""Deterministic voxel models, recipes, localizations and EnergyExchange rules."""
import json, pathlib
from PIL import Image, ImageDraw
ROOT=pathlib.Path(__file__).resolve().parents[1]/'src/main/resources'
def write(path,obj):
 p=ROOT/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(obj,ensure_ascii=False,indent=2)+'\n')
entries=[
('blast_rocket','赤焰·爆破烟花','Ember Breach Rocket','ED764B',{'firework_rocket':1,'gunpowder':2,'iron_ingot':1},1,'可破坏地形；爆炸伤害上限 10 点。','Breaks terrain when enabled; explosion capped at 10 HP.'),
('safe_rocket','碧潮·安全爆破烟花','Tidal Safe-Blast Rocket','4ADACB',{'firework_rocket':1,'gunpowder':2,'iron_ingot':1,'slime_ball':1},1,'爆炸伤害上限 10 点；始终不破坏地形。','Explosion capped at 10 HP; never breaks terrain.'),
('halo_rocket','金轮·环形烟花','Gilded Halo Rocket','EEC66D',{'firework_rocket':1,'glowstone_dust':1,'gold_nugget':1},1,'绽放金色光环；无伤害。','A golden halo; no damage.'),
('willow_rocket','流萤·垂柳烟花','Firefly Willow Rocket','B6E875',{'firework_rocket':1,'glowstone_dust':1,'slime_ball':1},1,'绽放垂柳光枝；无伤害。','Cascading willow branches; no damage.'),
('spiral_rocket','星旋·螺旋烟花','Astral Spiral Rocket','BE8CF0',{'firework_rocket':1,'amethyst_shard':1,'redstone':1},1,'绽放紫色螺旋；无伤害。','A violet spiral; no damage.'),
('star_rocket','星芒·星形烟花','Starlight Star Rocket','89CFFF',{'firework_rocket':1,'glowstone_dust':1,'amethyst_shard':1},1,'绽放冰蓝五角星；无伤害。','An ice-blue five-point star; no damage.'),
('heart_rocket','绯愿·爱心烟花','Blush Heart Rocket','F28BAE',{'firework_rocket':1,'redstone':1,'slime_ball':1},1,'绽放粉色爱心；无伤害。','A pink heart; no damage.'),
('blast_arrow','赤焰·爆破箭','Emberburst Arrow','ED764B',{'arrow':4,'gunpowder':1,'iron_ingot':1},4,'命中爆炸，上限 6 点伤害；不破坏地形。','Impact explosion capped at 6 HP; no terrain damage.'),
('frost_arrow','霜晶·冰霜箭','Frostglass Arrow','7EDDF4',{'arrow':4,'snowball':2,'amethyst_shard':1},4,'直击 4 点伤害；附近目标缓慢 II，持续 3 秒。','4 HP direct hit; nearby targets receive Slowness II for 3s.'),
('pulse_arrow','紫电·震荡箭','Violet Pulse Arrow','BE8CF0',{'arrow':4,'slime_ball':1,'amethyst_shard':1,'redstone':1},4,'直击 4 点伤害；小范围击退，可借地形战斗。','4 HP direct hit; a small radial knockback pulse.'),
('mark_arrow','曙光·标记箭','Dawnmark Arrow','EEC66D',{'arrow':4,'glowstone_dust':1},4,'直击 4 点伤害；附近目标发光 8 秒。','4 HP direct hit; nearby targets glow for 8s.'),
('venom_arrow','蚀叶·毒雾箭','Witherleaf Venom Arrow','81C75A',{'arrow':4,'spider_eye':1,'slime_ball':1},4,'直击 4 点；2.5 格内中毒 I，持续 3 秒；不穿墙。','4 HP direct hit; Poison I within 2.5 blocks for 3s; blocked by walls.'),
('gravity_arrow','引星·牵引箭','Starward Gravity Arrow','7588DC',{'arrow':4,'ender_pearl':1,'amethyst_shard':1},4,'直击 4 点；将 3 格内目标水平拉向落点；不穿墙。','4 HP direct hit; pulls targets within 3 blocks horizontally toward impact; blocked by walls.')]
# Equal purchase/salvage unit prices rounded DOWN from ingredient value/output.
# Never make crafting yield more energy than its input.
prices={'arrow':33,'firework_rocket':64,'gunpowder':192,'paper':64,'iron_ingot':252,'copper_ingot':8,'amethyst_shard':64,'snowball':64,'glowstone_dust':64,'slime_ball':8,'feather':64,'gold_nugget':227,'redstone':64,'spider_eye':64,'ender_pearl':1024}
zh={'itemGroup.starfall':'星火工坊','modmenu.nameTranslation.starfall':'星火工坊'}
en={'itemGroup.starfall':'Starfall Workshop','modmenu.nameTranslation.starfall':'Starfall Workshop'}
for id,cn,name,color,ingredients,count,tip,engtip in entries:
 rocket=id.endswith('rocket'); rgb=tuple(bytes.fromhex(color))
 # A deliberately pixel-art metal palette atlas; JSON elements address swatches.
 im=Image.new('RGB',(16,16)); d=ImageDraw.Draw(im)
 colors=[(34,43,57),(66,79,93),rgb,tuple(min(255,int(v*.6+100)) for v in rgb),(235,212,157),(128,93,53),(233,239,226),(109,72,49)]
 for i,c in enumerate(colors): d.rectangle((i*2,0,i*2+1,15),fill=c)
 p=ROOT/f'assets/starfall/textures/item/{id}.png';p.parent.mkdir(parents=True,exist_ok=True);im.save(p)
 def box(a,b,swatch):
  return {'from':a,'to':b,'faces':{f:{'texture':'#all','uv':[swatch*2,0,swatch*2+1,1]} for f in ['north','south','east','west','up','down']}}
 if rocket:
  els=[box([6,3,6],[10,12,10],0),box([6.2,5,5.8],[9.8,10,6.2],2),box([6.2,5,9.8],[9.8,10,10.2],2),box([5.8,5,6.2],[6.2,10,9.8],2),box([9.8,5,6.2],[10.2,10,9.8],2),box([5.8,4,5.8],[10.2,5,10.2],4),box([5.8,10,5.8],[10.2,11,10.2],4),box([6,12,6],[10,13,10],2),box([6.5,13,6.5],[9.5,14,9.5],2),box([7,14,7],[9,15,9],3),box([7.5,15,7.5],[8.5,16,8.5],4),box([7,1.5,7],[9,3,9],1),box([7.5,-1,7.5],[8.5,1.5,8.5],7)]
  for a,b in [([4,1,7.5],[6,5,8.5]),([10,1,7.5],[12,5,8.5]),([7.5,1,4],[8.5,5,6]),([7.5,1,10],[8.5,5,12])]:els.append(box(a,b,2))
  # Identification windows distinguish safe/ornamental shells beyond color.
  if id=='safe_rocket': els.extend([box([7.6,6,5.6],[8.4,9,5.9],6),box([6.6,7,5.6],[9.4,8,5.9],6)])
  elif id=='blast_rocket':els.append(box([7,6.5,5.5],[9,8.5,5.9],0))
  elif id=='halo_rocket':els.extend([box([5.5,7,5.5],[10.5,7.6,6],4),box([5.5,7,10],[10.5,7.6,10.5],4)])
  elif id=='star_rocket':
   # Bright cross/star badge, readable even without the blue shell color.
   els.extend([box([7.6,6,5.4],[8.4,9.5,5.8],6),box([6.3,7.3,5.4],[9.7,8.1,5.8],6)])
  elif id=='heart_rocket':
   for a,b in [([6.5,7.5,5.4],[7.7,8.8,5.8]),([8.3,7.5,5.4],[9.5,8.8,5.8]),([7.1,6.8,5.4],[8.9,8,5.8]),([7.6,6.2,5.4],[8.4,6.8,5.8])]:els.append(box(a,b,6))
 else:
  els=[box([7.55,-1,7.55],[8.45,11,8.45],7),box([7.35,8.8,7.35],[8.65,10,8.65],4),box([6.5,10,7],[9.5,12,9],2),box([7,12,7.2],[9,14,8.8],3),box([7.5,14,7.5],[8.5,15,8.5],6),box([5.5,0,7.8],[7.55,4,8.2],2),box([8.45,0,7.8],[10.5,4,8.2],2),box([7.8,0,5.5],[8.2,4,7.55],6),box([7.8,0,8.45],[8.2,4,10.5],6)]
  if id=='blast_arrow':els.append(box([6.2,10,6.2],[9.8,12,9.8],0))
  if id=='pulse_arrow':els.append(box([5.8,11,7.7],[10.2,11.6,8.3],4))
  if id=='venom_arrow':
   # Paired barbs distinguish the poison head from the other arrows.
   els.extend([box([5.8,9.5,7.3],[6.8,12.5,8.7],2),box([9.2,9.5,7.3],[10.2,12.5,8.7],2)])
  if id=='gravity_arrow':
   els.extend([box([5.8,10,6.3],[6.4,13.5,9.7],4),box([9.6,10,6.3],[10.2,13.5,9.7],4),box([6.4,13,7.3],[9.6,13.5,8.7],4)])
 model={'credit':'Original procedural voxel model by Starfall Workshop','textures':{'all':f'starfall:item/{id}','particle':f'starfall:item/{id}'},'elements':els,'display':{'gui':{'rotation':[20,-35,-20],'translation':[0,0,0],'scale':[.9,.9,.9]},'ground':{'rotation':[0,0,0],'translation':[0,3,0],'scale':[.5,.5,.5]},'fixed':{'rotation':[0,0,-30],'translation':[0,0,0],'scale':[.8,.8,.8]},'firstperson_righthand':{'rotation':[0,-90,15],'translation':[1,1,1],'scale':[.65,.65,.65]},'thirdperson_righthand':{'rotation':[0,0,0],'translation':[0,1,0],'scale':[.65,.65,.65]}}}
 write(f'assets/starfall/models/item/{id}.json',model)
 write(f'assets/starfall/items/{id}.json',{'model':{'type':'minecraft:model','model':f'starfall:item/{id}'}})
 write(f'data/starfall/recipe/{id}.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':[f'minecraft:{k}' for k,n in ingredients.items() for _ in range(n)],'result':{'id':f'starfall:{id}','count':count}})
 write(f'data/starfall/advancement/recipes/{id}.json',{'parent':'minecraft:recipes/root','criteria':{'has_material':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':f'minecraft:{next(iter(ingredients))}'}]}},'has_the_recipe':{'trigger':'minecraft:recipe_unlocked','conditions':{'recipe':f'starfall:{id}'}}},'requirements':[['has_material','has_the_recipe']],'rewards':{'recipes':[f'starfall:{id}']}})
 value=sum(prices[k]*v for k,v in ingredients.items())//count
 write(f'data/starfall/energyexchange/values/{id}.json',{'value':str(value)})
 zh[f'item.starfall.{id}']=cn;en[f'item.starfall.{id}']=name
 zh[f'tip.starfall.{id}']=tip;en[f'tip.starfall.{id}']=engtip
for key,cn,eng in [
('tip.starfall.consumable','一次性弹药；不享受无限；命中后消耗。','Consumable ammunition; no Infinity; consumed on impact.'),
('tip.starfall.display','可放置、用弩或发射器发射，也可为鞘翅加速。','Launch by hand, crossbow or dispenser; supports elytra boosting.'),
('tip.starfall.crossbow','支持弩、发射器和放置；不能为鞘翅加速。','Crossbow, dispenser or ground launch; no elytra boosting.'),
('config.starfall.title','星火工坊 · 设置','Starfall Workshop · Settings'),
('config.starfall.terrain','普通爆破破坏地形：%s','Breach terrain damage: %s'),
('config.starfall.damage','伤害倍率：%s','Damage multiplier: %s'),
('config.starfall.power','烟花爆炸强度：%s','Rocket explosion power: %s'),
('config.starfall.particles','图案基础粒子数：%s','Base pattern particles: %s'),
('config.starfall.save','保存','Save'),('config.starfall.cancel','取消','Cancel'),
('config.starfall.hint','安全爆破始终不破坏地形；能量价格由数据包配置。','Safe blasts never break terrain. Energy prices use data packs.'),
('config.starfall.remote','此处为本地预设；多人规则由服务器配置。','Local presets shown. Multiplayer rules are configured by the server.'),
('config.starfall.error','保存失败，请检查配置文件权限。','Save failed. Check config file permissions.')]: zh[key]=cn;en[key]=eng
write('assets/starfall/lang/zh_cn.json',zh);write('assets/starfall/lang/en_us.json',en)
write('data/minecraft/tags/item/arrows.json',{'replace':False,'values':['starfall:'+e[0] for e in entries if e[0].endswith('arrow')]})
im=Image.new('RGBA',(128,128),(19,26,39,255));d=ImageDraw.Draw(im)
d.ellipse((10,10,118,118),outline='#dabe80',width=3);d.polygon([(62,15),(79,38),(79,91),(47,91),(47,38)],fill='#4adacb');d.rectangle((47,40,79,47),fill='#eec66d');d.rectangle((47,80,79,88),fill='#eec66d');d.polygon([(52,91),(62,119),(74,91)],fill='#ed764b');d.polygon([(47,70),(32,101),(47,94)],fill='#be8cf0');d.polygon([(79,70),(94,101),(79,94)],fill='#be8cf0');im.save(ROOT/'assets/starfall/icon.png')
print(f'Generated {len(entries)} items, models, recipes and price rules.')
