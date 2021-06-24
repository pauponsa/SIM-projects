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

ActiveRecord::Schema.define(version: 2021_06_02_164808) do

  create_table "entrada_usuarios", force: :cascade do |t|
    t.integer "user_id"
    t.integer "evento_id"
    t.string "code"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.boolean "ha_participat", default: false
    t.index ["evento_id"], name: "index_entrada_usuarios_on_evento_id"
    t.index ["user_id"], name: "index_entrada_usuarios_on_user_id"
  end

  create_table "event_images", force: :cascade do |t|
    t.integer "evento_id", null: false
    t.string "image"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["evento_id"], name: "index_event_images_on_evento_id"
  end

  create_table "event_tags", force: :cascade do |t|
    t.integer "evento_id"
    t.integer "tag_id"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["evento_id"], name: "index_event_tags_on_evento_id"
    t.index ["tag_id"], name: "index_event_tags_on_tag_id"
  end

  create_table "eventos", force: :cascade do |t|
    t.string "title"
    t.string "description"
    t.string "start_date"
    t.string "end_date"
    t.integer "capacity"
    t.string "latitude"
    t.string "longitude"
    t.integer "participants"
    t.string "price"
    t.string "URL_share"
    t.string "URL_page"
    t.string "start_time"
    t.string "end_time"
    t.integer "id_creator"
    t.integer "reports"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.string "author"
  end

  create_table "favourites", force: :cascade do |t|
    t.integer "user_id"
    t.integer "evento_id"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["evento_id"], name: "index_favourites_on_evento_id"
    t.index ["user_id"], name: "index_favourites_on_user_id"
  end

  create_table "followers", force: :cascade do |t|
    t.integer "company_id"
    t.integer "customer_id"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["company_id"], name: "index_followers_on_company_id"
    t.index ["customer_id"], name: "index_followers_on_customer_id"
  end

  create_table "ratings", force: :cascade do |t|
    t.decimal "rating", precision: 4, scale: 3, default: "0.0"
    t.string "text"
    t.integer "company_id"
    t.integer "customer_id"
    t.integer "evento_id"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["company_id"], name: "index_ratings_on_company_id"
    t.index ["customer_id"], name: "index_ratings_on_customer_id"
    t.index ["evento_id"], name: "index_ratings_on_evento_id"
  end

  create_table "tags", force: :cascade do |t|
    t.string "tag_name"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
  end

  create_table "users", force: :cascade do |t|
    t.string "email", null: false
    t.string "password_digest"
    t.string "name"
    t.string "username"
    t.string "phone"
    t.string "nif"
    t.string "image"
    t.string "language"
    t.string "longitude"
    t.string "latitude"
    t.string "login_token"
    t.string "role", default: "customer", null: false
    t.decimal "rating", precision: 4, scale: 3, default: "0.0"
    t.string "description"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.string "password_reset_token"
    t.datetime "password_reset_send_at"
    t.index ["email"], name: "index_users_on_email", unique: true
    t.index ["login_token"], name: "index_users_on_login_token", unique: true
  end

  create_table "usuari_reports", force: :cascade do |t|
    t.integer "user_id"
    t.integer "evento_id"
    t.index ["evento_id"], name: "index_usuari_reports_on_evento_id"
    t.index ["user_id"], name: "index_usuari_reports_on_user_id"
  end

  add_foreign_key "event_images", "eventos"
  add_foreign_key "followers", "users", column: "company_id"
  add_foreign_key "followers", "users", column: "customer_id"
  add_foreign_key "ratings", "eventos"
  add_foreign_key "ratings", "users", column: "company_id"
  add_foreign_key "ratings", "users", column: "customer_id"
end
