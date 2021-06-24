require "test_helper"

class EventosControllerTest < ActionDispatch::IntegrationTest
  setup do
    @evento = eventos(:two)
    @user = users(:two)
  end

  test "should get index" do
    get eventos_url,  as: :json
    assert_response :success
  end

  test "should get company events" do
      get '/evento_comp/2',  as: :json
      assert_response 200
  end

  test "should create evento" do
    #first we will create a new user
    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_response = JSON.parse(@response.body)
    #then we will log him up
    post '/login', params: { email: "company@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)
    #finally we'll try to create an event as a company
    assert_difference('Evento.count') do
      post eventos_url, params: { token: login_response["login_token"],  title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date, capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, start_time: @evento.start_time, end_time: @evento.end_time, id_creator: company_response["id"] }, as: :json
    end
    assert_response 201
  end



  test "should show evento" do
    get eventos_url(@evento), as: :json
    assert_response :success
  end

   test "should update evento" do
     #first we will create a new user
     post users_url, params: { email: "companyUpdate@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
     assert_response :success
     company_response = JSON.parse(@response.body)
     #then we will log him up
     post '/login', params: { email: "companyUpdate@gmail.com", password: "123456789" }, as: :json
     assert_response :success
     login_response = JSON.parse(@response.body)
     #once the user is created we create a new event
     assert_difference('Evento.count') do
       post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"], title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date, capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, URL_page: nil, URL_share: nil, start_time: @evento.start_time, end_time: @evento.end_time  }, as: :json
     end
     evento_id = JSON.parse(@response.body)["id"]
     #finally we'll try to update an event information as a company
     @evento = eventos(:one)
     put evento_url(evento_id), params: { token: login_response["login_token"], title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date, capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, URL_page: nil, URL_share: nil, start_time: @evento.start_time, end_time: @evento.end_time  }, as: :json
     assert_response 200
  end

  test "should report events" do

    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_response = JSON.parse(@response.body)
    #then we will log him up
    post '/login', params: { email: "company@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)
    #finally we'll try to create an event as a company
    assert_difference('Evento.count') do
      post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"], itle: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date,  capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, URL_page: nil, URL_share: nil, start_time: @evento.start_time, end_time: @evento.end_time }, as: :json
    end
    evento_id = JSON.parse(@response.body)["id"]

    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    assert_response :success
    customer_resp = JSON.parse(@response.body) 

    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)

      put '/report/'+evento_id.to_s,  params: { token:login_response["login_token"]}, as: :json
      assert_response 200
  end

  test "should destroy evento" do
    #first we will create a new user
    post users_url, params: { email: "companyDestroy@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_response = JSON.parse(@response.body)
    #then we will log him up
    post '/login', params: { email: "companyDestroy@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)
    #once the user is created we create a new event
    assert_difference('Evento.count') do
      post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"], title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date,  capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, URL_page: nil, URL_share: nil, start_time: @evento.start_time, end_time: @evento.end_time  }, as: :json
    end
    evento_id = JSON.parse(@response.body)["id"]
    #finally we'll try to update an event information as a company
    assert_difference('Evento.count', -1) do
      delete evento_url(evento_id), params: {token: login_response["login_token"], id: evento_id}, as: :json
    end
  end

end
