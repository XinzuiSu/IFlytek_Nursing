package com.iflytek.medicalsdk_nursing.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.iflytek.android.framework.db.DbHelper;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.domain.OptionDic;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;

import java.util.List;

/**
 * @Title: com.iflytek.dao.PaintInfoDao
 * @Copyright: IFlytek Co., Ltd. Copyright 16/3/31-下午2:57,  All rights reserved
 * @Description: TODO 选项字典数据库操作类;
 * @author: chenzhilei
 * @data: 16/3/31 下午2:57
 * @version: V1.0
 */
public class OptionDicDao {
    /**
     * 数据库操作类
     */
    DbHelper db;

    public OptionDicDao(Context context) {
        db = IFlyNursing.getInstance().getDbHelper();
        db.checkOrCreateTable(OptionDic.class);
    }


    /**
     * 批量插入选项信息到数据库中
     *
     * @param list
     */
    public boolean saveOrUpdateOptionDicList(List<OptionDic> list) {
        if (null == list || list.size() <= 0) {
            return false;
        }
        db.checkOrCreateTable(PatientInfo.class);
        SQLiteDatabase db1 = db.getDb();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("insert into IFLY_OPTION");
            sql.append("(codeID,optCode,optName,parentOpt)");
            sql.append("values(?,?,?,?)");

            SQLiteStatement stat = db1.compileStatement(sql.toString());
            db1.beginTransaction();
            for (OptionDic info : list) {
                stat.bindString(1, StringUtils.nullStrToEmpty(info.getCodeID()));
                stat.bindString(2, StringUtils.nullStrToEmpty(info.getOptCode()));
                stat.bindString(3, StringUtils.nullStrToEmpty(info.getOptName()));
                stat.bindString(4, StringUtils.nullStrToEmpty(info.getParentOpt()));
                long result = stat.executeInsert();
                if (result < 0) {
                    return false;
                }
            }
            db1.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (null != db) {
                    db1.endTransaction();
                    // db1.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 根据选项名称获取选项信息
     * @param optName
     * @return
     */
    public OptionDic getOptionDic(String optName) {
        OptionDic optionDic = db.queryFrist(OptionDic.class, "optName = ?", optName);
        return optionDic;
    }



    /**
     * 获取所有文书信息
     *
     * @return
     */
    public List<OptionDic> getOptionDicList() {
        List<OptionDic> optionDics = db.queryList(OptionDic.class, "", "");
        return optionDics;
    }



    /**
     * 清空文书数据
     *
     * @return
     */
    public boolean deleteOptionDic() {
        Boolean result = false;
        try {
            db.getDb().execSQL("delete from IFLY_OPTION");
            result = true;
        } catch (Exception e) {
            return result;
        }
        return result;
    }


    /**
     * 查询文书总数
     *
     * @return
     */
    public int countNum() {
        Cursor c = db.getDb().rawQuery(
                "select count(*) from IFLY_OPTION ",
                null);
        Integer count = 0;
        if (c.moveToNext()) {
            count = c.getInt(0);
        }
        c.close();
        c = null;
        return count;
    }
}
