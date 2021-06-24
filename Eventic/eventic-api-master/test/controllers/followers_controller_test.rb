require "test_helper"

class FollowersControllerTest < ActionDispatch::IntegrationTest
  
  setup do
    
  end
  
	test "should create follower" do
    #first we create company
    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    company_resp = JSON.parse(@response.body)
    assert_response :success   
    #Then we will create a new user
    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    customer_resp = JSON.parse(@response.body) 
    assert_response :success  
   
    #Then we will log him up
    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
    login_response = JSON.parse(@response.body)
    assert_response :success
    
    #Finally we'll try to create follower as a customer
    assert_difference('Follower.count') do
      post followers_url, params: { token:login_response["login_token"], company_id: company_resp["id"], customer_id: customer_resp["id"]}, as: :json
    end
    assert_response 201
  end
  
  test "should destroy follower" do
    #first we create company
    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_resp = JSON.parse(@response.body)
    #Then we will create a new user
    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    assert_response :success
    customer_resp = JSON.parse(@response.body) 
    #Then we will log him up
    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)
    #Now we'll try to create an event as a customer
    assert_difference('Follower.count') do
      post followers_url, params: { token:login_response["login_token"], company_id: company_resp["id"], customer_id: customer_resp["id"]}, as: :json
    end
    assert_response 201
    #Finally we'll try to update an event information as a customer
    assert_difference('Follower.count', -1) do
      delete '/follower', params: {token: login_response["login_token"], company_id: company_resp["id"], customer_id: customer_resp["id"]}, as: :json
    end
  end

end
