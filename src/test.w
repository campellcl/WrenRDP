program foo is
  var i,j : integer;
  var b : boolean;
begin
  i := 12;
  b :=: true;
  b :=: [true or false];
  b :=: (3+i) < 5;
  b :=: 3+i < 5;
  b :=: [3+i < 5];
  b :=: i+3 < 5;
  b :=: b;
  skip;
  read j;
  write i+1*j/3-i;
  while b or b and b do
    if b then skip end if
  end while;
  if i < 13 and i>3 then skip else skip end if;
  if not [ i<>13 or i<=13 or i>=3] then skip end if;
  while true do skip end while;
  while false do skip end while
end
