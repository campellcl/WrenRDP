//
// product computation using Russian peasant algorithm
// Fenwick,Norris,Kurtz
//

program product is
    var a,b,p : integer;
begin
    read a; read b; p := 0; 
    while b > 0 do
        if (b - (b/2) * 2) > 0 then 
            p := p + a
        end if;
        a := a * 2; 
        b := b / 2
    end while;
    write p 
end
