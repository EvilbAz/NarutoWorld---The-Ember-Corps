package embercorps;
import java.util.*;
import static embercorps.RuntimeReflect.*;
public class StaminaRuntime {
 static class State{double value=100,max=100;int used=-1000,dash=-1000,ground=-1000,charged=-1000;int observed=-1;boolean sheltered;}
 private static final Map<Object,State> states=new IdentityHashMap<>();
 private static State state(Object p){State s=states.get(p);if(s==null){s=new State();try{if(ServerRules.recovery(p)>0)s.value=0;}catch(Exception e){throw new IllegalStateException(e);}states.put(p,s);}try{int t=time(p);if(t<s.observed){s.used=-1000;s.dash=-1000;s.ground=-1000;s.charged=-1000;}s.observed=t;}catch(Exception e){throw new IllegalStateException(e);}return s;}
 private static int time(Object p)throws Exception{return ((Number)field(p,"field_70173_aa")).intValue();}
 private static boolean free(Object p)throws Exception{return (Boolean)field(field(p,"field_71075_bZ"),"field_75098_d")||(Boolean)call(p,"func_175149_v");}
 public static void forget(Object p){states.remove(p);}
 public static void clear(){states.clear();}
 public static void requestDash(Object p){try{if(client(p)){StaminaMod.channel.sendToServer(new StaminaMod.DashRequest());}else serverDash(p);}catch(Exception e){throw new IllegalStateException(e);}}
 public static void serverDash(Object p){try{
  if(!(Boolean)call(p,"func_70089_S"))return;State s=state(p);int t=time(p);if(t-s.dash<6||!((Boolean)call(p,"func_70093_af"))||(!(Boolean)field(p,"field_70122_E")&&t-s.ground>6))return;
  double x=num(field(p,"field_70165_t")),y=num(field(p,"field_70163_u")),z=num(field(p,"field_70161_v"));
  call(Class.forName("net.narutomod.procedure.ProcedureOnLivingJump"),"emberNativeLunge",p);
  if(s.dash!=t)return;Object connection=field(p,"field_71135_a");
  if(x!=num(field(p,"field_70165_t"))||y!=num(field(p,"field_70163_u"))||z!=num(field(p,"field_70161_v")))call(connection,"func_147364_a",num(field(p,"field_70165_t")),num(field(p,"field_70163_u")),num(field(p,"field_70161_v")),field(p,"field_70177_z"),field(p,"field_70125_A"));
  Object velocity=Class.forName("net.minecraft.network.play.server.SPacketEntityVelocity").getConstructor(Class.forName("net.minecraft.entity.Entity")).newInstance(p);call(connection,"func_147359_a",velocity);sync(p,s);dashSound(p);
 }catch(Exception e){throw new IllegalStateException("Ember dash",e);}}
 private static boolean soundReported;
 private static void dashSound(Object p){try{
  Object registry=Class.forName("net.minecraft.util.SoundEvent").getField("field_187505_a").get(null);
  Object key=Class.forName("net.minecraft.util.ResourceLocation").getConstructor(String.class).newInstance("narutomod:movement");
  Object sound=call(registry,"func_82594_a",key);
  if(sound==null)throw new IllegalStateException("Missing narutomod:movement");
  Class<?> category=Class.forName("net.minecraft.util.SoundCategory");Object players=Enum.valueOf((Class)category,"PLAYERS");
  call(field(p,"field_70170_p"),"func_184148_a",null,num(field(p,"field_70165_t")),num(field(p,"field_70163_u")),num(field(p,"field_70161_v")),sound,players,0.65f,1.0f);
 }catch(Exception e){if(!soundReported){soundReported=true;System.err.println("[Ember Dash Sound] "+e);}}}
 public static int dashGate(Object p){try{State s=state(p);int t=time(p);if(t-s.dash<6)return 0;if(!free(p)&&s.value<12)return 0;if(!free(p))s.value-=12;s.used=t;s.dash=t;return 20;}catch(Exception e){throw new IllegalStateException(e);}}
 public static boolean charge(Object p){try{
  if(!player(p)||client(p)||free(p))return true;State s=state(p);int t=time(p);if(s.charged==t)return true;s.charged=t;
  if(s.value<0.4){call(p,"func_184602_cy");sync(p,s);return false;}s.value-=0.4;s.used=t;return true;
 }catch(Exception e){throw new IllegalStateException("Ember charge",e);}}
 public static void tick(Object p)throws Exception{
  if(client(p))return;State s=state(p);int t=time(p);if((Boolean)field(p,"field_70122_E"))s.ground=t;
  if(t%20==0){double xp=num(call(Class.forName("net.narutomod.PlayerTracker"),"getBattleXp",p));s.max=StaminaMath.capacity(xp,((Number)field(p,"field_71068_ca")).intValue());if(ServerRules.recovery(p)>0)s.max*=0.75;s.value=Math.min(s.value,s.max);s.sheltered=shelter(p);}
  if(t-s.used>=30)s.value=StaminaMath.regenerate(s.value,s.max,s.sheltered);if(free(p))s.value=s.max;
  if(t%4==0)sync(p,s);
 }
 private static boolean shelter(Object p)throws Exception{
  if(((Number)field(p,"field_70737_aN")).intValue()>0)return false;
  Object w=field(p,"field_70170_p"),pos=call(p,"func_180425_c");if((Boolean)call(w,"func_175678_i",pos))return false;
  for(int x=-3;x<=3;x++)for(int y=-1;y<=1;y++)for(int z=-3;z<=3;z++){Object b=call(call(w,"func_180495_p",call(pos,"func_177982_a",x,y,z)),"func_177230_c");if("net.minecraft.block.BlockBed".equals(b.getClass().getName()))return true;}return false;
 }
 private static void sync(Object p,State s)throws Exception{
  Class<?> sync=Class.forName("net.narutomod.procedure.ProcedureSync$EntityNBTTag");
  call(sync,"sendToSelf",p,"EmberStamina",s.value);call(sync,"sendToSelf",p,"EmberStaminaMax",s.max);call(sync,"sendToSelf",p,"EmberSheltered",s.sheltered?1.0:0.0);
 }
}