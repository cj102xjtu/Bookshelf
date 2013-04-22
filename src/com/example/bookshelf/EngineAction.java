package com.example.bookshelf;

import java.io.Serializable;

import android.os.Messenger;

public class EngineAction implements Serializable{
    private static final long serialVersionUID = -7060210535500464481L;
    public static final int GET_BOOKS_INFO = 0;
    public static final int LOAN_A_BOOK = 1;
    public static final int RETURN_A_BOOK = 2;
    
    int mActionType ;
    String mBookId;
    String mUserId;
    
    public EngineAction(int actionType)
    {
        this(actionType, "", "");
    }
    
    public EngineAction(int actionType, String bookId, String userId)
    {
        mActionType = actionType;
        mBookId = bookId;
        mUserId = userId;
    }
    
    public void excute(Engine engine, Messenger messager)
    {
        switch (mActionType) {
        case GET_BOOKS_INFO:
            engine.getBooksInfo(messager);
            break;
        case LOAN_A_BOOK:
            engine.loanOrReturnBook(mBookId, mUserId, true, messager);
            break;
        case RETURN_A_BOOK:
            engine.loanOrReturnBook(mBookId, mUserId, false, messager);
            break;

        default:
            break;
        }
    }
    
}
