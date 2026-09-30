package embercorps;
import net.minecraftforge.fml.common.Mod;import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.*;import net.minecraftforge.fml.relauncher.Side;import io.netty.buffer.ByteBuf;
@Mod(modid="embercorpsstamina",name="Ember Corps Stamina",version="0.2.5",dependencies="required-after:narutomod")
public class StaminaMod {
 public static SimpleNetworkWrapper channel;
 @Mod.EventHandler public void init(FMLInitializationEvent e){channel=NetworkRegistry.INSTANCE.newSimpleChannel("emberstamina");channel.registerMessage(DashHandler.class,DashRequest.class,0,Side.SERVER);MinecraftForge.EVENT_BUS.register(this);MinecraftForge.EVENT_BUS.register(new ServerRules());}
 @Mod.EventHandler public void started(net.minecraftforge.fml.common.event.FMLServerStartedEvent e){try{ServerRules.removeCommands(RuntimeReflect.call(net.minecraftforge.fml.common.FMLCommonHandler.instance(),"getMinecraftServerInstance"));}catch(Exception x){throw new IllegalStateException(x);}}
 @SubscribeEvent public void tick(TickEvent.PlayerTickEvent e){if(e.phase==TickEvent.Phase.END)try{StaminaRuntime.tick(RuntimeReflect.field(e,"player"));}catch(Exception ex){throw new IllegalStateException("Ember stamina tick",ex);}}
 @SubscribeEvent public void clone(net.minecraftforge.event.entity.player.PlayerEvent.Clone e){try{StaminaRuntime.forget(RuntimeReflect.call(e,"getOriginal"));StaminaRuntime.forget(RuntimeReflect.call(e,"getEntityPlayer"));}catch(Exception x){throw new IllegalStateException(x);}}
 @SubscribeEvent public void logout(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent e){try{StaminaRuntime.forget(RuntimeReflect.field(e,"player"));}catch(Exception x){throw new IllegalStateException(x);}}
 @Mod.EventHandler public void stopped(net.minecraftforge.fml.common.event.FMLServerStoppedEvent e){StaminaRuntime.clear();}
 public static class DashRequest implements IMessage {public void fromBytes(ByteBuf b){}public void toBytes(ByteBuf b){}}
 public static class DashHandler implements IMessageHandler<DashRequest,IMessage>{public IMessage onMessage(DashRequest m,MessageContext c){
  try{final Object p=c.getServerHandler().player;Object w=RuntimeReflect.call(p,"func_71121_q");RuntimeReflect.call(w,"func_152344_a",(Runnable)()->StaminaRuntime.serverDash(p));}catch(Exception e){throw new IllegalStateException(e);}return null;
 }}
}
