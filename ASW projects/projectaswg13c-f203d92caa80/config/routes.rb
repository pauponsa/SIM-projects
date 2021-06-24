Rails.application.routes.draw do

  resources :liked_comments
  resources :liked_posts
  resources :comentaris
   get '/submit/newest', to: 'submits#newest', as: 'newest_submit'
   get '/submit/index', to: 'submits#index', as: 'index_submit'
   post '/submits/comment', to: 'submits#comment', as: 'submits_comment'
   get '/comentaris/index', to: 'comentaris#index', as: 'index_comentaris'
   get '/submit/ask', to: 'submits#ask', as: 'ask_submit'

  
  resources :comentaris do
    put 'upvote_comment_update', on: :member
    put 'vote_comment', on: :member
    put 'unvote_comment', on: :member
  end

  resources :users

  resources :liked_submits
   get '/submit/newest', to: 'submits#newest', as: 'newest_submit2'
   get '/submit/index', to: 'submits#index', as: 'index_submit2'
   get '/users/:id/submits', to: 'users#submits'
   get '/users/:id/comments', to: 'users#comments'
   get '/users/:id/comments', to: 'users#comments.json'
   get '/users/:id/upvoted_submissions', to: 'users#upvoted_submissions'
   get '/users/:id/upvoted_comments', to: 'users#upvoted_comments'
   
  resources :submits do
    put 'upvote_update', on: :member
    put 'vote', on: :member
    put 'unvote', on: :member
  end
  
  resources :usuaris
  # For details on the DSL available within this file, see https://guides.rubyonrails.org/routing.html
  get '/auth/google_oauth2/callback', to: 'sessions#omniauth'
  get '/logout', to: 'sessions#destroy', as: 'logout'
  get '/submits/new/auth/google_oauth2/callback', to: 'sessions#omniauth'
  
 
  root 'submits#index'
end
