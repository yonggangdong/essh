package com.eryansky.codegen;

import com.eryansky.codegen.db.DataSource;
import com.eryansky.codegen.db.DbConnection;
import com.eryansky.codegen.db.DbFactory;
import com.eryansky.codegen.vo.Table;

import java.util.List;

public class CodeGenMain {

    public static void main(String[] args) {
        List<Table> tables = null;
        Builder builder = null;
        DataSource db = null;
        String t = "T_SYS_%";//表 通配"%"
        Table table = null;
        try {
            db = DbFactory.create(new DbConnection().getConn());
            tables = db.getTables(t);
            builder = new Builder(tables);
            builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
