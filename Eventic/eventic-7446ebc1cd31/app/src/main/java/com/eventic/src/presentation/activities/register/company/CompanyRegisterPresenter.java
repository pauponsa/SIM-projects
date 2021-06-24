package com.eventic.src.presentation.activities.register.company;

import com.eventic.src.domain.Company;
import com.example.eventic.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompanyRegisterPresenter implements CompanyRegisterContract.Presenter{

    private CompanyRegisterContract.View mView;

    public CompanyRegisterPresenter(CompanyRegisterContract.View view) {
        mView = view;
    }


    @Override
    public void register(String companyName, String companyUsername, String email, String password, String repeatPassword, String role, boolean terms){
        if (companyName.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_company_name));
        else if (companyUsername.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_username));
        else if (email.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_email));
        else if (password.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_password));
        else if (repeatPassword.isEmpty()) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_repeat_password));
        else if (!terms) mView.showDialog(mView.getStringById(R.string.missing_parameters), mView.getStringById(R.string.missing_terms));
        else if (!password.equals(repeatPassword)) mView.showToast(mView.getStringById(R.string.passwords_not_equals));
        else {
            registerCompany(companyUsername, companyName, email, password, role);
        }
    }


    public void registerCompany(String username, String name, String email, String password, String role) {
        mView.setLoading(true);
        final Company company = new Company(username,name, email, password, password, role);

        //System.out.println("Registrando empresa " + email + " y password " + password);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CompanyRegisterContract.JsonHttpApi jsonHttpApi = retrofit.create(CompanyRegisterContract.JsonHttpApi.class);


        Call<Company> call = jsonHttpApi.registerCompany(company);

        call.enqueue(new Callback<Company>() {
            @Override
            public void onResponse(Call<Company> call, Response<Company> response) {
                mView.setLoading(false);
                Company companyResponse = response.body();
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    return;
                }
                mView.closeAndLogin(email, password);
            }

            @Override
            public void onFailure(Call<Company> call, Throwable t) {
                mView.setLoading(false);
                mView.failedConnection();
            }
        });
    }

    @Override
    public void signIn() {
        mView.changeToLogin();
    }
}
