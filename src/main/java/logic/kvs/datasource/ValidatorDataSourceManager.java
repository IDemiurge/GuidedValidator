package logic.kvs.datasource;

import javax.swing.*;

public class ValidatorDataSourceManager {

    // whether to prompt the user for file test datasource
    private final boolean testConfiguration = true;

    /**
     * @return {@link ValidatorDataSource} object to be used by the application
     */
    public ValidatorDataSource initDataSource() {
        if (!testConfiguration){
            return new PresetValidatorDataSource();
        }
        int i = JOptionPane.showConfirmDialog(null, "Use file test datasource?");
        boolean test = i == JOptionPane.OK_OPTION;
        ValidatorDataSource dataSource;
        if (test){
            dataSource = new TestValidatorDataSource();
        } else {
            dataSource = new PresetValidatorDataSource();
        }
        return dataSource;
    }
}
