package com.eventic.src.presentation.main;

public class MainPresenter implements MainContract.Presenter {


    private MainContract.View mView;

    public MainPresenter(MainContract.View view) {
        mView = view;
    }


    @Override
    public boolean checkIfSignedIn() {
        //if(user and password in DB) return true;
        return false;
    }

    @Override
    public void viewCreated() {
        mView.changeToSignIn();
    }

    @Override
    public void dontReturn() {mView.finishActivity();}
}
