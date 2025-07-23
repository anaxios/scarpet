//!scarpet v1.5

// stay loaded
__config() -> (
   m(
      l('stay_loaded','true')
   )
);

__on_player_dies(player) -> (
  if(__holding_feather(player)
  , inventory_remove(player, 'feather', 1);
    __store_player_inventory(player);
    __stop_items_from_dropping(player); 
  ); 
);


__on_player_respawns(player) -> (
  if(__stored_player_inventory_exists(player)
  , schedule(0, '__restore_player_inventory', player);
    schedule(2,'__delete_stored_player_inventory', player);
  );
);

__holding_feather(player) -> (
  offhand = query(player,'holds','offhand');
  mainhand = query(player,'holds');
  (offhand:0 == 'feather' || mainhand:0 == 'feather');
);

__stored_player_inventory_exists(player) -> (
  // use !! to turn into boolean value. Sure why not.
  !(! read_file(player+'_feather_inv', 'json'));
);

__store_player_inventory(player) -> (
  i = []; 
  loop(inventory_size(player)
  , i += encode_json(inventory_get(player,_));
  );
  write_file(player+'_feather_inv', 'json', i);
);


__stop_items_from_dropping(player) -> (
  loop(inventory_size(player), inventory_set(player,_,0));
);


__restore_player_inventory(player) -> (
  i = read_file(player+'_feather_inv', 'json');
  for(decode_json(i)
  , if(_:0 != null
  , inventory_set(player,_i,_:1,_:0,_:2)
    );
  );
);


__delete_stored_player_inventory(player) -> (
  delete_file(player+'_feather_inv', 'json');
);


