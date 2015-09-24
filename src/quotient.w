//
// quotient computation using Russian peasant algorithm
// Fenwick,Norris,Kurtz
//

program quotient is
  var x,y,r,q,w : integer;
begin
    read x;
    read y;
    r := x;
    q :=0 ;
    w :=y ;
    while w <= r do
        w := 2 * w
    end while;
    while w > y do
        q := q * 2;
        w := w / 2;
        if w <= r then
            r := r - w; 
	    q := q + 1
        end if 
    end while;
    write q;
    write r 
end
