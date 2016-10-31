package com.iflytek.medicalsdk_nursing.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.iflytek.android.framework.db.DbHelper;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.domain.MappingInfo;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;

import java.util.List;

/**
 * @Title: com.iflytek.dao.PaintInfoDao
 * @Copyright: IFlytek Co., Ltd. Copyright 16/3/31-下午2:57,  All rights reserved
 * @Description: TODO 映射关系数据库操作类;
 * @author: chenzhilei
 * @data: 16/3/31 下午2:57
 * @version: V1.0
 */
public class MappingDao {
    /**
     * 数据库操作类
     */
    DbHelper db;

    public MappingDao(Context context) {
        db = IFlyNursing.getInstance().getDbHelper();
        db.checkOrCreateTable(MappingInfo.class);
    }


    /**
     * 批量插入映射关系数据信息到数据库中
     *
     * @param list
     */
    public boolean saveOrUpdateMappingInfoList(List<MappingInfo> list) {
        if (null == list || list.size() <= 0) {
            return false;
        }
        db.checkOrCreateTable(PatientInfo.class);
        SQLiteDatabase db1 = db.getDb();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("insert into IFLY_MAPPING");
            sql.append("(key,value)");
            sql.append("values(?,?)");

            SQLiteStatement stat = db1.compileStatement(sql.toString());
            db1.beginTransaction();
            for (MappingInfo info : list) {
                stat.bindString(1, StringUtils.nullStrToEmpty(info.getKey()));
                stat.bindString(2, StringUtils.nullStrToEmpty(info.getValue()));
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
     * 根据映射名称获取映射信息
     * @param key
     * @return
     */
    public MappingInfo getMappingDic(String key) {
        MappingInfo mappingInfo = db.queryFrist(MappingInfo.class, "key = ?", key);
        return mappingInfo;
    }



    /**
     * 获取所有映射关系信息
     *
     * @return
     */
    public List<MappingInfo> getMappingInfoList() {
        List<MappingInfo> mappingInfos = db.queryList(MappingInfo.class, "", "");
        return mappingInfos;
    }



    /**
     * 清空文书数据
     *
     * @return
     */
    public boolean deleteMappingInfo() {
        Boolean result = false;
        try {
            db.getDb().execSQL("delete from IFLY_MAPPING");
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
                "select count(*) from IFLY_MAPPING",
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
