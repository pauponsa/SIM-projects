package com.eventic.src.presentation.main;

public interface MainContract {

    /* Represents the View in MVP. */
    interface View {
        void changeToSignIn();

        void finishActivity();

    }

    /* Represents the Presenter in MVP. */
    interface Presenter {
        boolean checkIfSignedIn();

        void viewCreated();

        void dontReturn();
    }

}
