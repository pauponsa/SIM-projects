require "test_helper"

class FavouritesControllerTest < ActionDispatch::IntegrationTest
  setup do
    @evento = eventos(:two)
  end

  test "should get index" do
    get favourites_url, as: :json
    assert_response :success
  end

  test "should create favourite" do

    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_response = JSON.parse(@response.body)

    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    customer_resp = JSON.parse(@response.body)
    assert_response :success
    #Then we will log him up
    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
     login_response = JSON.parse(@response.body)
    assert_response :success

    assert_difference('Evento.count') do
      post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"], title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date, capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, URL_page: nil, URL_share: nil, start_time: @evento.start_time, end_time: @evento.end_time }, as: :json

    end
     evento_resp = JSON.parse(@response.body)

    assert_difference('Favourite.count') do
      post favourites_url, params: { token:login_response["login_token"], evento_id: evento_resp["id"], user_id: customer_resp["id"]}, as: :json
    end

    assert_response :success
  end

  test "should show favourite" do
    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    company_resp = JSON.parse(@response.body)
    assert_response :success

    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    assert_response :success
    customer_resp = JSON.parse(@response.body)
    #Then we will log him up
    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)

    get '/like_event?token='+login_response["login_token"].to_s+'&evento_id='+company_resp["id"].to_s, as: :json
    assert_response :success
  end


  test "should destroy favourite" do

    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_response = JSON.parse(@response.body)

    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    assert_response :success
    customer_resp = JSON.parse(@response.body)
    #Then we will log him up
    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)

    assert_difference('Evento.count') do
      post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"] ,title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date, capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, URL_page: nil, URL_share: nil, start_time: @evento.start_time, end_time: @evento.end_time }, as: :json

    end
     evento_resp = JSON.parse(@response.body)

    assert_difference('Favourite.count') do
      post favourites_url, params: { token:login_response["login_token"], evento_id: evento_resp["id"], user_id: customer_resp["id"]}, as: :json
    end

    assert_difference('Favourite.count', -1) do
      delete '/favourites', params: {token:login_response["login_token"], evento_id: evento_resp["id"], user_id: customer_resp["id"]}, as: :json
    end

    assert_response :success
  end
end
