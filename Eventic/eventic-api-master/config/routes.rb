Rails.application.routes.draw do

  resources :ratings
  resources :favourites
  resources :entrada_usuarios
  resources :event_tags
  resources :tags
  resources :eventos
  resources :followers
  post '/follower', to:'followers#create'
  get 'evento_comp/:id_creator', to:'eventos#comp'
  get 'evento', to:'eventos#index'
  put 'evento/:id', to: 'eventos#update'
  put 'report/:id', to: 'eventos#report'
  get 'evento/:id', to: 'eventos#show'
  get 'eventotag/:id', to: 'event_tags#show_tags'
  delete 'evento/:id', to: 'eventos#destroy'
  post 'crearevento', to:'eventos#create'
  delete '/event_tags', to: 'event_tags#destroy'
  delete '/follower', to: 'followers#destroy'
  get '/aviscas', to:'users#avis_cas'
  get 'liked/:user_id', to: 'favourites#liked'
  get 'followed_events', to: 'followers#followed_events'
  get 'report/:id', to:  'eventos#reported'

  resources :password_resets

  post '/password_resets/:id', to: 'password_resets#update'
  get '/editp/:id', to: 'password_resets#edit'
  get '/edit/', to: 'users#edit'


  get 'entrada_usuarios/:user_id', to:'entrada_usuarios#show'
  get 'entrada_usuarios', to:'entrada_usuarios#index'
  post 'entrada_usuarios', to:'entrada_usuarios#create'
  delete 'entrada_usuarios', to:'entrada_usuarios#destroy'
  get 'part_evento/:evento_id', to: 'entrada_usuarios#show_tickets_event'
  get 'participa', to:'entrada_usuarios#participa'
  put 'participa', to: 'entrada_usuarios#ha_participat'
  get 'es_participant', to:'entrada_usuarios#es_participant'

  post 'favourites', to: 'favourites#create'
  delete 'favourites', to: 'favourites#destroy'
  get 'like_event', to:'favourites#show'
  get 'follow_company', to: 'followers#followed'
  get 'ratings_company', to:'ratings#ratings_company'
  get 'numvaloracions', to:'ratings#numvaloracions'
  get 'rating_user_evento', to: 'ratings#rating_user_evento'
  resources :users
  get 'user', to: 'users#show'
  post 'login', to: 'users#login'
  post 'logout', to: 'users#logout'
  delete 'profile_pic', to: 'users#delete_profile_pic'
  get 'es_confirmat', to: 'entrada_usuarios#es_confirmat'
  
  get '/', to: 'application#index'

end
