"""Orthographic overview rendered directly from shipped voxel JSON geometry."""
import pathlib,json,math,sys
import numpy as np
from PIL import Image,ImageDraw,ImageFont
r=pathlib.Path(__file__).resolve().parents[1];res=r/'src/main/resources';out=r/'docs/model-preview.png'
fontpath=pathlib.Path(sys.argv[1]) if len(sys.argv)>1 else pathlib.Path('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf')
def font(n):
 f=ImageFont.truetype(str(fontpath),n)
 try:f.set_variation_by_axes([450])
 except OSError:pass
 return f
S=2;W,H=1440,2250
im=Image.new('RGB',(W*S,H*S),'#101923');d=ImageDraw.Draw(im)
def text(x,y,t,size=22,fill='#ecede8'):d.text((x*S,y*S),t,font=font(size*S),fill=fill)
def rect(box,fill,radius=18,outline=None):d.rounded_rectangle(tuple(v*S for v in box),radius=radius*S,fill=fill,outline=outline,width=1*S)
text(48,30,'星火工坊',44,'#efdaa5');text(49,92,'48 件分级弹药  /  Minecraft 26.2 · Fabric',21,'#96a9b7')
text(49,127,'下图由模组实际模型文件渲染；非游戏截图。',17,'#748996')
lang=json.loads((res/'assets/starfall/lang/zh_cn.json').read_text())
def render(id,cx,cy,scale=10):
 m=json.loads((res/f'assets/starfall/models/item/{id}.json').read_text());texture=Image.open(res/('assets/'+m['textures']['all'].replace(':','/textures/')+'.png')).convert('RGB')
 # View from above and to the side; y is Minecraft vertical.
 az=math.radians(32);el=math.radians(18)
 def rot(p):
  x,y,z=p[0]-8,p[1]-7,p[2]-8
  a=x*math.cos(az)-z*math.sin(az); b=x*math.sin(az)+z*math.cos(az)
  return a, -y*math.cos(el)-b*math.sin(el),y*math.sin(el)-b*math.cos(el)
 faces=[]
 for e in m['elements']:
  x,y,z=e['from'];X,Y,Z=e['to']
  for name,verts,shade in [('north',[(x,y,z),(X,y,z),(X,Y,z),(x,Y,z)],.7),('south',[(x,y,Z),(X,y,Z),(X,Y,Z),(x,Y,Z)],1),('east',[(X,y,z),(X,y,Z),(X,Y,Z),(X,Y,z)],.8),('west',[(x,y,z),(x,y,Z),(x,Y,Z),(x,Y,z)],.62),('up',[(x,Y,z),(X,Y,z),(X,Y,Z),(x,Y,Z)],1.14),('down',[(x,y,z),(X,y,z),(X,y,Z),(x,y,Z)],.5)]:
   uv=e['faces'][name]['uv'];rgb=texture.getpixel((int(uv[0]),0));pts=[rot(v) for v in verts]
   faces.append(([(int((cx+p[0]*scale)*S),int((cy+p[1]*scale)*S),p[2]) for p in pts],tuple(min(255,int(c*shade)) for c in rgb)))
 # Z-buffer resolves intersecting collars, panels and fin geometry faithfully.
 x0=int((cx-100)*S);y0=int((cy-115)*S);ww=200*S;hh=230*S
 pixels=np.array(im.crop((x0,y0,x0+ww,y0+hh)));depth=np.full((hh,ww),-np.inf)
 for pts,c in faces:
  for inds in [(0,1,2),(0,2,3)]:
   v=[(pts[i][0]-x0,pts[i][1]-y0,pts[i][2]) for i in inds]
   (ax,ay,az),(bx,by,bz),(cx1,cy1,cz)=v
   den=(by-cy1)*(ax-cx1)+(cx1-bx)*(ay-cy1)
   if abs(den)<1e-8:continue
   left=max(0,min(q[0] for q in v));right=min(ww-1,max(q[0] for q in v));top=max(0,min(q[1] for q in v));bottom=min(hh-1,max(q[1] for q in v))
   if left>right or top>bottom:continue
   yy,xx=np.mgrid[top:bottom+1,left:right+1]
   a=((by-cy1)*(xx-cx1)+(cx1-bx)*(yy-cy1))/den
   b=((cy1-ay)*(xx-cx1)+(ax-cx1)*(yy-cy1))/den;cc=1-a-b
   z=a*az+b*bz+cc*cz;buf=depth[top:bottom+1,left:right+1]
   mask=(a>=0)&(b>=0)&(cc>=0)&(z>buf)
   buf[mask]=z[mask];pixels[top:bottom+1,left:right+1][mask]=c
 im.paste(Image.fromarray(pixels),(x0,y0))
ids=['blast_rocket','safe_rocket','halo_rocket','willow_rocket','spiral_rocket','star_rocket','heart_rocket','blast_arrow','frost_arrow','pulse_arrow','mark_arrow','venom_arrow','gravity_arrow']
labels=['破坏地形 · 16.5 点伤害上限','保护地形 · 16.5 点伤害上限','金色光环 · 无伤害','垂柳光枝 · 无伤害','星云螺旋 · 无伤害','冰蓝五角星 · 无伤害','粉色爱心 · 无伤害','9.9 点爆破 · 不破坏地形','缓慢 II · 6 秒','小范围击退','发光标记 · 16 秒','中毒 I · 6 秒','水平牵引 · 不穿墙']
for i,id in enumerate(ids):
 col=i%3;row=i//3;x=48+col*452;y=185+row*280
 rect((x,y,x+436,y+264),'#1a2835')
 render('master_'+id,x+100,y+121,10.5)
 name=lang['item.starfall.'+id];a,b=name.split('·',1)
 text(x+197,y+61,a,26,'#efdaa5');text(x+197,y+104,b,23);text(x+197,y+150,labels[i],14,'#9aafbf');text(x+197,y+185,'展示：大师版模型',14,'#718b9c')
rect((48,1605,1392,1880),'#1b2e3b')
text(76,1627,'材料越多，效果越强',25,'#efdaa5');text(76,1670,'伤害 / 范围 / 控制时长',16,'#9aafbf');text(76,1700,'爆破可继续堆料至灾变 VI。',16,'#9aafbf');text(76,1761,'安全爆破所有等级均保护地形。',16,'#4adacb')
for i,(prefix,title,multipliers) in enumerate([('','基础 I','1.00 / 1.00 / 1.00'),('refined_','精制 II','1.35 / 1.15 / 1.50'),('master_','大师 III','1.65 / 1.30 / 2.00')]):
 cx=550+i*305;render(prefix+'safe_rocket',cx,1708,7.5);text(cx-49,1800,title,19);text(cx-82,1830,multipliers,15,'#9aafbf')
rect((48,1900,1392,2180),'#1b2e3b')
text(76,1920,'爆破专属 · 高投入高杀伤',25,'#efdaa5')
text(76,1970,'从大师逐级投入 TNT、金属块、钻石块与下界合金锭。',17,'#9aafbf')
for i,(prefix,title,caps) in enumerate([('heavy_','重装 IV','40 / 24 HP'),('siege_','攻城 V','120 / 72 HP'),('cataclysm_','灾变 VI','400 / 240 HP')]):
 cx=550+i*305;render(prefix+'safe_rocket',cx,2010,7.5);text(cx-49,2105,title,19);text(cx-80,2140,caps,15,'#efdaa5')
text(76,2025,'数值：烟花 / 箭矢伤害上限',15,'#9aafbf')
text(76,2070,'护甲、距离与遮挡会降低伤害。',15,'#9aafbf')
text(49,2210,'独立三维模型 · 中英双语 · Mod Menu 设置 · EnergyExchange 分级兑换',17,'#8196a5')
im.resize((W,H),Image.Resampling.LANCZOS).save(out)
print(out)
