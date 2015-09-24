program avg_a_list is
  var num, tot, count, avg : integer;
begin
  tot := 0;
  count := 0;
  num := 0;
  avg := 0;
  read num;
  while num <> -1 do
    count := count + 1;
    tot := tot + num;
    read num
  end while;
  if count <> 0 then
    avg := tot / count;
    write avg
  end if
end