# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# This file is the source Rails uses to define your schema when running `bin/rails
# db:schema:load`. When creating a new database, `bin/rails db:schema:load` tends to
# be faster and is potentially less error prone than running all of your
# migrations from scratch. Old migrations may fail to apply correctly if those
# migrations use external dependencies or application code.
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 2021_05_06_114914) do

  create_table "comentaris", force: :cascade do |t|
    t.string "text"
    t.integer "postID"
    t.integer "respondsToId"
    t.integer "likes"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.integer "user_id", null: false
    t.index ["user_id"], name: "index_comentaris_on_user_id"
  end

  create_table "liked_comments", force: :cascade do |t|
    t.integer "user_id", null: false
    t.integer "comentari_id", null: false
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["comentari_id"], name: "index_liked_comments_on_comentari_id"
    t.index ["user_id"], name: "index_liked_comments_on_user_id"
  end

  create_table "liked_posts", force: :cascade do |t|
    t.integer "submit_id", null: false
    t.integer "user_id", null: false
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["submit_id"], name: "index_liked_posts_on_submit_id"
    t.index ["user_id"], name: "index_liked_posts_on_user_id"
  end

  create_table "submits", force: :cascade do |t|
    t.string "title"
    t.string "URL"
    t.string "text"
    t.integer "like"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.integer "user_id", null: false
    t.index ["user_id"], name: "index_submits_on_user_id"
  end

  create_table "users", force: :cascade do |t|
    t.string "name"
    t.string "about", default: ""
    t.string "email"
    t.string "password_digest"
    t.string "provider"
    t.string "uid"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.integer "karma", default: 1
    t.string "ltoken"
  end

  add_foreign_key "comentaris", "users"
  add_foreign_key "liked_comments", "comentaris"
  add_foreign_key "liked_comments", "users"
  add_foreign_key "liked_posts", "submits"
  add_foreign_key "liked_posts", "users"
  add_foreign_key "submits", "users"
end
