create procedure test_proc(in arg string)
begin
    set result = 'test_result';
    print result || ',' || arg ;
end;
