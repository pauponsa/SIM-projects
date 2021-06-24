class AddHaParticipatToEntradaUsuarios < ActiveRecord::Migration[6.1]
  def change
    add_column :entrada_usuarios, :ha_participat, :boolean, :default => false
  end
end
