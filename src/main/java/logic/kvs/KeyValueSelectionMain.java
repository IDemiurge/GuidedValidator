package logic.kvs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import logic.kvs.datasource.ValidatorDataSource;
import logic.kvs.datasource.ValidatorDataSourceManager;
import util.MapUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class KeyValueSelectionMain {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    KeyValueSelectionMain window = new KeyValueSelectionMain();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public KeyValueSelectionMain() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        KeyValueSelectionSet keyValueSet = new KeyValueSelectionSet() {
            private Map<String, List<String>> keyValueSet = MapUtils.newLinkedHashMap(
                    MapUtils.<String, List<String>>newEntry("Mode", Lists.newArrayList("Boss", "Skirmish", "Campaign")),
                    MapUtils.<String, List<String>>newEntry("Seals", Lists.newArrayList("0", "1")),
                    MapUtils.<String, List<String>>newEntry("Path", Lists.newArrayList("Shadow", "Radiant"))
            );

            @Override
            public List<String> getKeys() {
                return ImmutableList.copyOf(keyValueSet.keySet());
            }

            @Override
            public List<String> getValues(String key) {
                return ImmutableList.copyOf(keyValueSet.get(key));
            }
        };

        ValidatorDataSource dataSource = new ValidatorDataSourceManager().initDataSource();
        frame.add(new KeyValueSelectionPanel(keyValueSet, dataSource));
    }

}
