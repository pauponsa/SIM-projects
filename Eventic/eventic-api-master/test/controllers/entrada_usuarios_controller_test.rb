require "test_helper"

class EntradaUsuariosControllerTest < ActionDispatch::IntegrationTest
   setup do
     @entrada_usuario = entrada_usuarios(:one)
     @evento = eventos(:two)
     @user = users(:two)
    end

   test "should get index" do
     get entrada_usuarios_url(@entrada_usuario),  as: :json
     assert_response :success
    end

    test "should get entradas de usuario" do

     get entrada_usuarios_url(@user.id),  as: :json
      assert_response 200
    end

    test "should get entradas de un evento" do
      get '/part_evento/2', as: :json
      assert_response 200
    end


   test "should create entrada_usuario" do
    #first we will create a new user
    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_response = JSON.parse(@response.body)
    #Then we will create a new user
    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    customer_resp = JSON.parse(@response.body)
    assert_response :success
    #then we will log him up
    post '/login', params: { email: "company@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)
    #finally we'll try to create an event as a company
    assert_difference('Evento.count') do
      post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"], title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date, capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, URL_page: nil, URL_share: nil, start_time: @evento.start_time, end_time: @evento.end_time }, as: :json
    end
    post '/logout', params: { login_token: login_response["login_token"] }
    assert_response :success

    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response_cust = JSON.parse(@response.body)
    assert_difference('EntradaUsuario.count') do
      post entrada_usuarios_url, params: { token:login_response_cust["login_token"], evento_id: @evento.id}, as: :json
    end
    assert_response :success
  end

   test "should destroy entrada_usuario" do
     #first we will create a new user
    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_response = JSON.parse(@response.body)
    #Then we will create a new user
    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    customer_resp = JSON.parse(@response.body)
    assert_response :success
    #then we will log him up
    post '/login', params: { email: "company@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)
    #finally we'll try to create an event as a company
    assert_difference('Evento.count') do
      post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"], title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date, capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, URL_page: nil, URL_share: nil, start_time: @evento.start_time, end_time: @evento.end_time }, as: :json
    end
    post '/logout', params: { login_token: login_response["login_token"] }
    assert_response :success

    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
    login_response_cust = JSON.parse(@response.body)
    assert_response :success
    assert_difference('EntradaUsuario.count') do
      post entrada_usuarios_url, params: { token:login_response_cust["login_token"], evento_id: @evento.id}, as: :json
    end
    assert_difference('EntradaUsuario.count', -1) do
      delete '/entrada_usuarios', params: { token:login_response_cust["login_token"], evento_id: @evento.id}, as: :json
    end
  end
end
