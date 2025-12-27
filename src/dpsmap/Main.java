package dpsmap;

import arc.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

public class Main extends Mod{

    public Main(){
        Events.on(ClientLoadEvent.class, e -> {
            DpsVars.init();
            DpsSettings.init();
            DpsKeyBinds.init();
            DpsShaders.init();

//            showDpsDialog();
        });

        Events.run(Trigger.update, () -> {
            if(!Core.scene.hasField()){
                if(Core.input.keyRelease(DpsKeyBinds.enable)){
                    String key = DpsSettings.enable.toggle() ? "setting.enable" : "setting.disable";
                    String info = Core.bundle.format(key, DpsSettings.enable.localized());
                    Vars.ui.showInfoFade(info);
                }

                if(Core.input.keyRelease(DpsKeyBinds.showPlayerTeam)){
                    String key = DpsSettings.showPlayerTeam.toggle() ? "setting.enable" : "setting.disable";
                    String info = Core.bundle.format(key, DpsSettings.showPlayerTeam.localized());
                    Vars.ui.showInfoFade(info);
                }

                if(Core.input.keyRelease(DpsKeyBinds.nextTargetMode)){
                    int next = (DpsSettings.targetMode.get() + 1) % DpsTargetMode.all.length;
                    DpsTargetMode nextMode = DpsTargetMode.all[next];
                    DpsSettings.targetMode.set(next);
                    Vars.ui.showInfoFade(Core.bundle.format("dpsHeatmap.nextTargetMode", nextMode.localized()));
                }
            }

            DpsHeatmap.update();
        });

        Events.run(Trigger.postDraw, DpsHeatmap::draw);
    }
}
