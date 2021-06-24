class ApplicationController < ActionController::Base
  
  protect_from_forgery unless: -> { request.format.json? }
  before_action :set_submit, only: [:upvote_update, :vote, :unvote]
  before_action :set_comentari, only: [:upvote_comment_update, :vote_comment, :unvote_comment]
  
  def upvote_update
    if session[:user_id] != @submit.user_id
      liked_submit=LikedPost.where(user_id: session[:user_id], submit_id: params[:id])
      user = User.find(session[:user_id])
      
      if liked_submit==[]
        LikedPost.create(user_id: session[:user_id], submit_id: params[:id]).save
        @submit.like+=1
        user.update('karma': user.karma + 1)
        session[:user_karma] += 1
      else
        liked_submit.destroy_all
        @submit.like-=1
        user.update('karma': user.karma - 1)
        session[:user_karma] -= 1
      end
    end
    @submit.save
    redirect_back(fallback_location: root_path)
  end
  
  def upvote_comment_update
    if session[:user_id] != @comentari.user_id
      liked_comment=LikedComment.where(user_id: session[:user_id], comentari_id: params[:id])
      user = User.find(session[:user_id])
      
      if liked_comment==[]
        LikedComment.create(user_id: session[:user_id], comentari_id: params[:id]).save
        @comentari.likes+=1
        user.update('karma': user.karma + 1)
        session[:user_karma] += 1
      else
        liked_comment.destroy_all
        @comentari.likes-=1
        user.update('karma': user.karma - 1)
        session[:user_karma] -= 1
      end
    end
    @comentari.save
    redirect_back(fallback_location: root_path)
  end
  
  def vote 
    user = User.where(id: session[:user_id]).first
    respond_to do |format|
      format.json { 
        user = User.where(ltoken: request.headers["X-API-Key"]).first
        if request.headers["X-API-Key"].blank?
          render json: {
            "status": 401,
            "message": "You provided no api key (X-API-KEY Header)"
          }, status: 401
          return
        elsif user==nil
          render json: {
            "status": 403,
            "message": "Your api key (X-API-KEY Header) is not valid"
          }, status: 403
          return
  
        elsif user.id==@submit.user_id
          render json: {
            "status": 405,
            "message": "You can't vote your own submit"
          }, status: 405
          return
        end
      }
    end
    if user.id != @submit.user_id
      liked_submit=LikedPost.where(user_id: user.id, submit_id: params[:id])
      if liked_submit==[]
        LikedPost.create(user_id: user.id, submit_id: params[:id]).save
        @submit.like+=1
        user.update('karma': user.karma + 1)
        respond_to do |format|
          format.json { render json: {
            "status": 200,
            "message": "Submit voted successfully"
            }
          }
        end
      else
        respond_to do |format|
          format.json { render json: {
            "status": 405,
            "message": "The submit is already voted"
            }, status: 405
          }
        end
      end
    end
    @submit.save
  end
  
  def unvote
    user = User.where(id: session[:user_id]).first
    respond_to do |format|
      format.json { 
        user = User.where(ltoken: request.headers["X-API-Key"]).first
        if request.headers["X-API-Key"].blank?
          render json: {
            "status": 401,
            "message": "You provided no api key (X-API-KEY Header)"
          }, status: 401
          return
        elsif user==nil
          render json: {
            "status": 403,
            "message": "Your api key (X-API-KEY Header) is not valid"
          }, status: 403
          return
  
        elsif user.id==@submit.user_id
          render json: {
            "status": 405,
            "message": "You can't unvote your own submit"
          }, status: 405
          return
        end
      }
    end
    if user.id != @submit.user_id
      liked_submit=LikedPost.where(user_id: user.id, submit_id: params[:id])
      if liked_submit!=[]
        liked_submit.destroy_all
        @submit.like-=1
        user.update('karma': user.karma - 1)
        respond_to do |format|
          format.json { render json: {
            "status": 200,
            "message": "Submit unvoted successfully"
            }
          }
        end
      else
        respond_to do |format|
          format.json { render json: {
            "status": 405,
            "message": "The submit is already unvoted"
            }, status: 405
          }
        end
      end
    end
    @submit.save
  end
  
  def vote_comment
    user= User.where(id: session[:user_id]).first
    respond_to do |format|
      format.json { 
        user = User.where(ltoken: request.headers["X-API-Key"]).first
        if request.headers["X-API-Key"].blank?
          render json: {
            "status": 401,
            "message": "You provided no api key (X-API-KEY Header)"
          }, status: 401
          return
        elsif user==nil
          render json: {
            "status": 403,
            "message": "Your api key (X-API-KEY Header) is not valid"
          }, status: 403
          return
  
        elsif user.id==@comentari.user_id
          render json: {
            "status": 405,
            "message": "You can't vote your own comment"
          }, status: 405
          return
        end
      }
    end
    
    if user.id!=@comentari.user_id
      liked_comment=LikedComment.where(user_id: user.id, comentari_id: params[:id])
      
      if liked_comment==[]
        LikedComment.create(user_id: user.id, comentari_id: params[:id]).save
        @comentari.likes+=1
        user.update('karma': user.karma + 1)
        respond_to do |format|
          format.json { render json: {
            "status": 200,
            "message": "Comment voted successfully"
            }
          }
        end
      else
        respond_to do |format|
          format.json { render json: {
            "status": 405,
            "message": "The comment is already voted"
            }, status: 405
          }
        end
      end
      @comentari.save
    end
  end
  
  def unvote_comment
    user= User.where(id: session[:user_id]).first
    respond_to do |format|
      format.json { 
        user = User.where(ltoken: request.headers["X-API-Key"]).first
        if request.headers["X-API-Key"].blank?
          render json: {
            "status": 401,
            "message": "You provided no api key (X-API-KEY Header)"
          }, status: 401
          return
        elsif user==nil
          render json: {
            "status": 403,
            "message": "Your api key (X-API-KEY Header) is not valid"
          }, status: 403
          return
  
        elsif user.id==@comentari.user_id
          render json: {
            "status": 405,
            "message": "You can't unvote your own comment"
          }, status: 405
          return
        end
      }
    end
    
    if user.id!=@comentari.user_id
      liked_comment=LikedComment.where(user_id: user.id, comentari_id: params[:id])
      
      if liked_comment!=[]
        liked_comment.destroy_all
        @comentari.likes-=1
        user.update('karma': user.karma - 1)
        respond_to do |format|
          format.json { render json: {
            "status": 200,
            "message": "Comment unvoted successfully"
            }
          }
        end
      else
        respond_to do |format|
          format.json { render json: {
            "status": 405,
            "message": "The comment is already unvoted"
            }, status: 405
          }
        end
      end
      @comentari.save
    end
  end
    
  
  
  def not_found2
    respond_to do |format|
    format.html {raise ActionController::RoutingError.new('Not Found')}
    format.json  {
      render json: {
            "status": 404,
            "message": "Not found"
            },
            status: 404
      return
    }
    end
  end  
 
end
