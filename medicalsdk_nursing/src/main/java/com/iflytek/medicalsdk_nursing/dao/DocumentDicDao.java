package com.iflytek.medicalsdk_nursing.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.iflytek.android.framework.db.DbHelper;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.domain.DocumentDic;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;

import java.util.List;

/**
 * @Title: com.iflytek.dao.PaintInfoDao
 * @Copyright: IFlytek Co., Ltd. Copyright 16/3/31-下午2:57,  All rights reserved
 * @Description: TODO 文书字典数据库操作类;
 * @author: chenzhilei
 * @data: 16/3/31 下午2:57
 * @version: V1.0
 */
public class DocumentDicDao {
    /**
     * 数据库操作类
     */
    DbHelper db;

    public DocumentDicDao(Context context) {
        db = IFlyNursing.getInstance().getDbHelper();
        db.checkOrCreateTable(DocumentDic.class);
    }


    /**
     * 批量插入文书信息到数据库中
     *
     * @param list
     */
    public boolean saveOrUpdateDocumentDicList(List<DocumentDic> list) {
        if (null == list || list.size() <= 0) {
            return false;
        }
        db.checkOrCreateTable(PatientInfo.class);
        SQLiteDatabase db1 = db.getDb();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("insert into IFLY_DOCUMENT");
            sql.append("(nmrID,nmrName,interfaceName,controlType)");
            sql.append("values(?,?,?,?)");

            SQLiteStatement stat = db1.compileStatement(sql.toString());
            db1.beginTransaction();
            for (DocumentDic info : list) {
                stat.bindString(1, StringUtils.nullStrToEmpty(info.getNmrID()));
                stat.bindString(2, StringUtils.nullStrToEmpty(info.getNmrName()));
                stat.bindString(3, StringUtils.nullStrToEmpty(info.getInterfaceName()));
                stat.bindString(4, StringUtils.nullStrToEmpty(info.getControlType()));
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
     * 根据文书名称获取文书信息
     * @param nmrName
     * @return
     */
    public DocumentDic getDocumentDic(String nmrName) {
        DocumentDic documentDic = db.queryFrist(DocumentDic.class, "nmrName = ?", nmrName);
        return documentDic;
    }



    /**
     * 获取所有文书信息
     *
     * @return
     */
    public List<DocumentDic> getDocumentDicList() {
        List<DocumentDic> documentDics = db.queryList(DocumentDic.class, "", "");
        return documentDics;
    }



    /**
     * 清空文书数据
     *
     * @return
     */
    public boolean deleteDocumentDic() {
        Boolean result = false;
        try {
            db.getDb().execSQL("delete from IFLY_DOCUMENT");
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
                "select count(*) from IFLY_DOCUMENT ",
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
