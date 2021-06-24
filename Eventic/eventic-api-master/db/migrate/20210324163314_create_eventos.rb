class CreateEventos < ActiveRecord::Migration[6.1]
  def change
    create_table :eventos do |t|
      t.string :title
      t.string :description
      t.string :start_date
      t.string :end_date
      t.integer :capacity
      t.string :latitude
      t.string :longitude
      t.integer :participants
      t.string :price
      t.string :URL_share
      t.string :URL_page
      t.string :start_time
      t.string :end_time
      t.integer :id_creator
      t.integer :reports
      t.timestamps

    end
  end
end
