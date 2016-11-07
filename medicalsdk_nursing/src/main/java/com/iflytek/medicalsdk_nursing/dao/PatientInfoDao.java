package com.iflytek.medicalsdk_nursing.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.iflytek.android.framework.db.DbHelper;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;

import java.util.List;

/**
 * @Title: com.iflytek.dao.PaintInfoDao
 * @Copyright: IFlytek Co., Ltd. Copyright 16/3/31-下午2:57,  All rights reserved
 * @Description: TODO 患者信息数据库操作类;
 * @author: chenzhilei
 * @data: 16/3/31 下午2:57
 * @version: V1.0
 */
public class PatientInfoDao {
    /**
     * 数据库操作类
     */
    DbHelper db;

    public PatientInfoDao(Context context) {
        db = IFlyNursing.getInstance().getDbHelper();
        db.checkOrCreateTable(PatientInfo.class);
    }


    /**
     * 批量插入病人信息到数据库中
     *
     * @param list
     */
    public boolean saveOrUpdatePaintInfoList(List<PatientInfo> list) {
        if (null == list || list.size() <= 0) {
            return false;
        }
        db.checkOrCreateTable(PatientInfo.class);
        SQLiteDatabase db1 = db.getDb();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("insert into IFLY_PATIENT");
            sql.append("(age,cwdm,hzxm,patid,sex,syxh,yexh)");
            sql.append("values(?,?,?,?,?,?,?)");

            SQLiteStatement stat = db1.compileStatement(sql.toString());
            db1.beginTransaction();
            for (PatientInfo info : list) {
                stat.bindString(1, StringUtils.nullStrToEmpty(info.getAge()));
                stat.bindString(2, StringUtils.nullStrToEmpty(info.getCwdm()));
                stat.bindString(3, StringUtils.nullStrToEmpty(info.getHzxm()));
                stat.bindString(4, StringUtils.nullStrToEmpty(info.getPatid()));
                stat.bindString(5, StringUtils.nullStrToEmpty(info.getSex()));
                stat.bindString(6, StringUtils.nullStrToEmpty(info.getSyxh()));
                stat.bindString(7, StringUtils.nullStrToEmpty(info.getYexh()));
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
     * 根据床号获取患者信息
     *
     * @param bedNo
     * @return
     */
    public PatientInfo getPatientInfo(String bedNo) {
        PatientInfo patientInfo = db.queryFrist(PatientInfo.class, "cwdm = ?", bedNo);
        return patientInfo;
    }


    /**
     * 获取所有患者信息
     *
     * @return
     */
    public List<PatientInfo> getPatientInfoList() {
        List<PatientInfo> patientInfos = db.queryList(PatientInfo.class, "", "");
        return patientInfos;
    }


    /**
     * 床号或姓名搜索患者列表
     *
     * @return
     */
    public List<PatientInfo> getPatientInfoList(String searchText) {
        List<PatientInfo> patientInfos = db.queryList(PatientInfo.class, ":cwdm like ? or :hzxm like ?", "%"+searchText+"%","%"+searchText+"%");
        return patientInfos;
    }


    /**
     * 清空患者数据
     *
     * @return
     */
    public boolean deletePatientInfo() {
        Boolean result = false;
        try {
            db.getDb().execSQL("delete from IFLY_PATIENT");
            result = true;
        } catch (Exception e) {
            return result;
        }

        return result;
    }


    /**
     * 查询患者总数
     *
     * @return
     */
    public int countNum() {
        Cursor c = db.getDb().rawQuery(
                "select count(*) from IFLY_PATIENT ",
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
