//!scarpet v1.5

// stay loaded
__config() -> (
   m(
      l('stay_loaded','true')
   )
);

__on_start() -> (
  global_debug = false;
  global_last_holding = null;
  global_keep = 3;
  global_player_inv_size = 37; // don't change! Hardcoded 81s are floating around which will probably break.
  global_max_pages = 64;
  global_max_inventory_size = global_max_pages * global_player_inv_size;
  global_inventory = {
    'current_page' -> 0,
	'show_page' -> false,
    'inv' -> []
	
  };
  __inventory_load_from_disk(global_inventory, player());

);

__on_player_uses_item(player, item_tuple, hand) -> (
  if(item_tuple:0 == 'bundle'
  , 
  
  global_name = item_tuple:2:'display';
  name_i = global_name:'Name';
  name_display = slice(split(':', name_i):1,1,-3);
  
  if(!name_display, name_display = 'bundle');
  //inventory_set(player, query(player, 'selected_slot'), 1, 'stone');
  
  screen = create_screen(player,'generic_9x6', name_display, _(screen, player, action, data) -> (
      if(action == 'slot_update' && data:'slot' != player~'selected_slot'+81
      , //if(last_holding:0 != 'bundle', 
	  global_last_holding = inventory_get(screen, -1);
	  //);
        //print('holding: ' + last_inv + ' data ' + data);
      );
  
      if(action == 'quick_move' && data:'slot' == player~'selected_slot'+81
      , inventory_set(screen, data:'slot', 0);
        'cancel';
      );
      if(action == 'throw' && data:'slot' == player~'selected_slot'+81
      , inventory_set(screen, data:'slot', 0);
        'cancel';
      );
      if(action == 'slot_update' && data:'slot' == player~'selected_slot'+81
      , 
	    inventory_set(screen, data:'slot', 1, 'bundle');
		//if(global_last_holding:0 == 'bundle', global_last_holding = null);
		inventory_set(screen, -1, last_holding:1, last_holding:0, last_holding:2);
        'cancel';
      );
	  
	  if(action == 'pickup' && data:'slot' == player~'selected_slot'+81
      , printer('DEBUG', 'button presssed: ' + data:'button');
	    //inventory_set(screen, data:'slot', 1, 'bundle');
        inventory_set(screen, -1, 0);
		if(data:'button' == 1, global_inventory:'current_page' = (global_inventory:'current_page' + 1) % global_max_pages);
		if(data:'button' == 0, global_inventory:'current_page' = (global_inventory:'current_page' - 1) % global_max_pages);
		printer('INFO',(global_inventory:'current_page' + 1));
    display_title(player, 'clear', format('db text'));
		__screen_inventory_load(screen, global_inventory);
		sound('item.book.page_turn', pos(player));
        'cancel';
      );
	  
	  if(action == 'slot_update'
      , __screen_inventory_store(screen, global_inventory);
	  'cancel';
      );
      ////////////////////////////////////////////////////////////////////////////////////////////////////////////////// close
      if(action == 'close'
      , __screen_inventory_store(screen, global_inventory);
        sound('item.bundle.remove_one', pos(player));
      );
  ));
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// open
  task(_(outer(screen),outer(item_tuple),outer(player))->(
    if(screen_property(screen, 'open') == 'true'
    , __screen_inventory_load(screen, global_inventory);
	  sound('item.bundle.remove_one', pos(player));
	  printer('INFO',(global_inventory:'current_page' + 1));
    );
  ));
  );
);

__inventory_page_display(inventory, screen) -> (
  null
);

__inventory_load_from_disk(inventory, player) -> (
// TODO: catch error when no file on disk.
  if(read_file(player+'_backpack_inv', 'json')
  , global_inventory:'inv' = decode_json(read_file(player+'_backpack_inv', 'json')); 
  ); 
  printer('DEBUG', 'inventory loaded: ' + inventory:'inv');
);

__inventory_store_to_disk(inventory, player) -> (
  write_file(player+'_backpack_inv', 'json', encode_json(inventory:'inv'));
);

__screen_inventory_load(screen, inventory) -> (
// TODO: catch error when no file on disk.
  i = inventory:'inv'; 
  inv_slot = (inventory:'current_page' * (inventory_size(screen) - global_player_inv_size));
  loop(inventory_size(screen) - global_player_inv_size
  , if(inv_slot
    , inventory_set(screen, _, i:(inv_slot + _):1, i:(inv_slot + _):0, if(i:(inv_slot + _):2, i:(inv_slot + _):2)); 
    );
  );
  
);

__screen_inventory_store(screen, inventory) -> (
  inv_slot = (inventory:'current_page' * (inventory_size(screen) - global_player_inv_size));
  loop(inventory_size(screen) - global_player_inv_size
  , //printer('DEBUG', 'inventory store: ' + inventory_get(screen,_));
    put(inventory:'inv':(inv_slot + _), if(inventory_get(screen,_),inventory_get(screen,_)));
  );
  __inventory_store_to_disk(inventory, player());
);

__inventory_empty(inventory) -> (
  i = []; 
  loop(global_max_inventory_size
  , i += null; //['stone', (_ % 64), null]; 
  );
  inventory:'inv' = i;
  __inventory_store_to_disk(inventory, player());
);

__screen_inventory_delete() -> (
  files = list_files('', 'json');

  for(files,
      name = split('_', _);
      if(name:0 == player(),
          delete_file(_, 'json');
      );
  );
//  empty_invitory = '[
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null",
//    "null"
//  ]';
  //write_file(player()+'_backpack_inv', 'json', empty_invitory);
);


printer(type, message) -> (
  if(type == 'DEBUG' && global_debug
  , print('DEBUG: ' + message);
  , type == 'INFO' && global_inventory:'show_page'
  , print('PAGE: ' + message);
  );
);

__backpack_open(screen, player) -> (
  contents = __backpack_read_nbt(screen, player);
  loop(length(contents)
  , if ( true
	, inventory_set(screen,_,contents:_:1,contents:_:0, if(contents:_:2, contents:_:2));
	);

  );
);

__backpack_close(screen, player) -> (
  contents = inventory_get(player, query(player, 'selected_slot'));
  print('debug: ' + contents);
  inventory_set(player, query(player, 'selected_slot'), 1, 'bundle', parse_nbt(items));
);

__backpack_load_nbt(screen, player) -> (
  items = __nbt_builder(screen);
  // place filled bundle into hand
  inventory_set(player, query(player, 'selected_slot'), 1, 'bundle', parse_nbt(items));
  
  // TODO: add support for named backpacks
  //if(tag
  //	,  inventory_set(player, query(player, 'selected_slot'), 1, 'bundle', (items));
  //	,  global_name
  //	,  inventory_set(player, query(player, 'selected_slot'), 1, 'bundle', '{display:'+ global_name + '}');
  //);
);

__nbt_builder(screen) -> (
  tags = [];
  loop(inventory_size(screen) - global_player_inv_size
  ,	i = inventory_get(screen, _);
      if(i, 
        tag = '{Count: ';
  	    tag +=  i:1;
	    tag +=  'b, id: "';
  	    tag +=  i:0 + '"'; 
  	    if(i:2
  	 	 ,tag += ',tag:' + i:2;
  	    );
  	    tag += '}';
	    // format into proper nbt
	    put(tags, null, encode_nbt(tag));
      );
  );

  // place formatted items in bundle nbt slots 
  tags = join(',', tags);
  items = '{Items:[' 
  	+ str(tags) 
  	+ ']';

  // preserve bundle name  
  if(global_name
	  , items += ',display:' + global_name
  );
  
  items += '}';

  //return
  items;
);
