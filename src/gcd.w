//
// gcd computation using Euclid algorithm
// Fenwick,Norris,Kurtz
//

program gcd is
    var m,n : integer;
begin
    read m; read n;
    while m <> n do
        if m < n then
            n := n - m 
        else
            m := m - n 
        end if
    end while;
    write m 
end
