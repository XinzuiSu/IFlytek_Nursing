package com.iflytek.medicalsdk_nursing.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.iflytek.android.framework.db.DbHelper;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;

import java.util.List;

/**
 * @Title: com.iflytek.dao.PaintInfoDao
 * @Copyright: IFlytek Co., Ltd. Copyright 16/3/31-下午2:57,  All rights reserved
 * @Description: TODO 文书基本信息数据库操作类;
 * @author: chenzhilei
 * @data: 16/3/31 下午2:57
 * @version: V1.0
 */
public class DocumentDetailDicDao {
    /**
     * 数据库操作类
     */
    DbHelper db;

    public DocumentDetailDicDao(Context context) {
        db = IFlyNursing.getInstance().getDbHelper();
        db.checkOrCreateTable(DocumentDetailDic.class);
    }


    /**
     * 批量插入项目信息到数据库中
     *
     * @param list
     */
    public boolean saveOrUpdateDocumentDetailDicList(List<DocumentDetailDic> list) {
        if (null == list || list.size() <= 0) {
            return false;
        }
        db.checkOrCreateTable(PatientInfo.class);
        SQLiteDatabase db1 = db.getDb();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("insert into IFLY_DOCUMENT_DETAIL");
            sql.append("(nmrID,itemID,itemName,itemType,codeID,controlType)");
            sql.append("values(?,?,?,?,?,?)");

            SQLiteStatement stat = db1.compileStatement(sql.toString());
            db1.beginTransaction();
            for (DocumentDetailDic info : list) {
                stat.bindString(1, StringUtils.nullStrToEmpty(info.getNmrID()));
                stat.bindString(2, StringUtils.nullStrToEmpty(info.getItemID()));
                stat.bindString(3, StringUtils.nullStrToEmpty(info.getItemName()));
                stat.bindString(4, StringUtils.nullStrToEmpty(info.getItemType()));
                stat.bindString(5, StringUtils.nullStrToEmpty(info.getCodeID()));
                stat.bindString(6, StringUtils.nullStrToEmpty(info.getControlType()));
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
     * 根据项目名称获取项目信息
     * @param itemName
     * @return
     */
    public DocumentDetailDic getDocumentDetailDic(String itemName) {
        DocumentDetailDic documentDetailDic = db.queryFrist(DocumentDetailDic.class, "itemName = ?", itemName);
        return documentDetailDic;
    }



    /**
     * 根据项目编号获取所有项目信息
     *
     * @return
     */
    public List<DocumentDetailDic> getItemDicList(String nmrID) {
        List<DocumentDetailDic> documentDetailDics = db.queryList(DocumentDetailDic.class, "nmrID = ?", nmrID);
        return documentDetailDics;
    }


    /**
     * 根据项目编号获取所有项目信息
     *
     * @return
     */
    public List<DocumentDetailDic> getItemDicList() {
        List<DocumentDetailDic> documentDetailDics = db.queryList(DocumentDetailDic.class, "", "");
        return documentDetailDics;
    }



    /**
     * 清空项目数据
     *
     * @return
     */
    public boolean deleteItemDic() {
        Boolean result = false;
        try {
            db.getDb().execSQL("delete from IFLY_DOCUMENT_DETAIL");
            result = true;
        } catch (Exception e) {
            return result;
        }

        return result;
    }


    /**
     * 查询项目总数
     *
     * @return
     */
    public int countNum() {
        Cursor c = db.getDb().rawQuery(
                "select count(*) from IFLY_DOCUMENT_DETAIL ",
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
