package dpsmap;

import arc.*;
import arc.func.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;

public class DpsSettings{
    public static SettingValue<Boolean> enable, showPlayerTeam;
    public static SettingValue<Integer> minDamage, maxDamage;
    public static SettingValue<Integer> intensify, alpha;
    public static SettingValue<Integer> targetMode, entityMode;

    public static void init(){
        enable = new SettingValue<>("dpsHeatmapEnable", true);
        showPlayerTeam = new SettingValue<>("dpsHeatmapShowPlayerTeam", true);
        minDamage = new SettingValue<>("dpsHeatmapMinDamage", 10);
        maxDamage = new SettingValue<>("dpsHeatmapMaxDamage", 3000);
        intensify = new SettingValue<>("dpsHeatmapIntensify", 100);
        alpha = new SettingValue<>("dpsHeatmapAlpha", 75);

        targetMode = new SettingValue<>("dpsHeatmapTargetMode", DpsTargetMode.both.ordinal());
        entityMode = new SettingValue<>("dpsHeatmapEntityMode", DpsEntityMode.both.ordinal());

        settings();

        targetMode.reset();
    }

    public static void settings(){
        SettingsTable graphics = Vars.ui.settings.graphics;

        graphics.getSettings().add(new Divider("dpsHeatmap", "@dpsHeatmap"));

        graphics.getSettings().add(new CustomTable(t -> {
            t.button("@dps-table.view", Styles.cleart, DpsSettings::showDpsDialog).height(48f).grow();
        }));

        graphics.checkPref(enable.name, enable.defaultValue);
        graphics.checkPref(showPlayerTeam.name, showPlayerTeam.defaultValue);
        graphics.sliderPref(minDamage.name, minDamage.defaultValue, 0, 200, 10, n -> "" + n);
        graphics.sliderPref(maxDamage.name, maxDamage.defaultValue, 200, 2_0000, 1000, n -> "" + n);
        graphics.sliderPref(intensify.name, intensify.defaultValue, 0, 200, 25, n -> n + "%");
        graphics.sliderPref(alpha.name, alpha.defaultValue, 0, 100, 5, n -> n + "%");

        graphics.sliderPref(targetMode.name, targetMode.defaultValue, 0, DpsTargetMode.all.length - 1, 1, n -> DpsTargetMode.all[n].localized());
        graphics.sliderPref(entityMode.name, entityMode.defaultValue, 0, DpsEntityMode.all.length - 1, 1, n -> DpsEntityMode.all[n].localized());
    }

    private static void showDpsDialog(){
        BaseDialog dialog = new BaseDialog("@dps-table");
        Table table = dialog.cont;
        table.add("@dps-table.notice").pad(8f).expandX().row();
        table.pane(t -> {
            int i = 0;
            for(UnitType type : Vars.content.units().copy().sort(UnitType::estimateDps)){
                t.image(type.uiIcon).size(Vars.iconSmall).scaling(Scaling.fit);
                t.add(Strings.autoFixed(type.estimateDps(), 2)).style(Styles.outlineLabel).pad(8f);
                if(++i % 4 == 0){
                    t.row();
                }
            }
        });
        dialog.addCloseButton();
        dialog.show();
    }

    public static class SettingValue<T>{
        public final String name;
        private final T defaultValue;

        public SettingValue(String name, T defaultValue){
            this.name = name;
            this.defaultValue = defaultValue;
            Core.settings.defaults(name, defaultValue);
        }

        public T get(){
            return get(defaultValue);
        }

        public T get(T def){
            return (T)Core.settings.get(name, def);
        }

        public void set(T value){
            Core.settings.put(name, value);
        }

        public boolean toggle(){
            if(defaultValue instanceof Boolean){
                boolean newValue = !(boolean)get();
                Core.settings.put(name, newValue);
                return newValue;
            }
            return false;
        }

        public void reset(){
            Core.settings.put(name, defaultValue);
        }

        public String localized(){
            return Core.bundle.get("setting." + name + ".name");
        }
    }

    private static class CustomTable extends Setting{
        private final Cons<Table> consumer;

        public CustomTable(Cons<Table> consumer){
            super("");

            this.consumer = consumer;
        }

        @Override
        public void add(SettingsTable table){
            table.table(consumer).grow().padTop(8f).row();
        }
    }

    private static class Divider extends Setting{
        public Divider(String name, String title){
            super(name);
            this.title = title;
        }

        @Override
        public void add(SettingsTable table){
            table.add(title).color(Pal.accent).colspan(4).pad(10).padTop(15).padBottom(4).row();
            table.image().color(Pal.accent).fillX().height(3).colspan(4).padTop(0).padBottom(10).row();
        }
    }

    public enum DpsTargetMode{
        ground, fly, both;

        public static final DpsTargetMode[] all = values();

        public String localized(){
            return Core.bundle.get("dpsHeatmap.targetMode." + name() + ".name", name());
        }
    }

    public enum DpsEntityMode{
        turret, unit, both;

        public static final DpsEntityMode[] all = values();

        public String localized(){
            return Core.bundle.get("dpsHeatmap.entityMode." + name() + ".name", name());
        }
    }
}
