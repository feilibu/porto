delete from stocks;

/*  

payload.txt
--
__VIEWSTATE=%2FwEPDwULLTIwNjcxMDQ3MTQPZBYCZg9kFgICBA9kFgQCDw9kFgICAg8WAh4HVmlzaWJsZWdkAhEPZBYCAgEPDxYCHgRUZXh0BSlCYXNjdWxlciBzdXIgbGEgdmVyc2lvbiBjbGFzc2lxdWUgZHUgc2l0ZWRkGAEFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYqBRVjdGwwMCRCb2R5QUJDJHhjYWM0MHAFFmN0bDAwJEJvZHlBQkMkeHNiZjEyMHAFFWN0bDAwJEJvZHlBQkMkeGNhY2F0cAUWY3RsMDAkQm9keUFCQyR4Y2FjbjIwcAUYY3RsMDAkQm9keUFCQyR4Y2Fjc21hbGxwBRVjdGwwMCRCb2R5QUJDJHhjYWM2MHAFFmN0bDAwJEJvZHlBQkMkeGNhY2w2MHAFFWN0bDAwJEJvZHlBQkMkeGNhY21zcAUVY3RsMDAkQm9keUFCQyR4YmVsMjBnBRVjdGwwMCRCb2R5QUJDJHhhZXgyNW4FEWN0bDAwJEJvZHlBQkMkZGp1BRJjdGwwMCRCb2R5QUJDJG5hc3UFFGN0bDAwJEJvZHlBQkMkc3A1MDB1BRZjdGwwMCRCb2R5QUJDJGdlcm1hbnlmBRFjdGwwMCRCb2R5QUJDJHVrZQUSY3RsMDAkQm9keUFCQyRiZWxnBRJjdGwwMCRCb2R5QUJDJGRldnAFFGN0bDAwJEJvZHlBQkMkc3BhaW5tBRVjdGwwMCRCb2R5QUJDJGl0YWxpYWkFE2N0bDAwJEJvZHlBQkMkaG9sbG4FFWN0bDAwJEJvZHlBQkMkbGlzYm9hbAUUY3RsMDAkQm9keUFCQyRzd2l0enMFEmN0bDAwJEJvZHlBQkMkdXNhdQUUY3RsMDAkQm9keUFCQyRhbHRlcnAFEWN0bDAwJEJvZHlBQkMkYnNwBRhjdGwwMCRCb2R5QUJDJGV1cm9saXN0QXAFGGN0bDAwJEJvZHlBQkMkZXVyb2xpc3RCcAUYY3RsMDAkQm9keUFCQyRldXJvbGlzdENwBRljdGwwMCRCb2R5QUJDJGV1cm9saXN0emVwBRpjdGwwMCRCb2R5QUJDJGV1cm9saXN0aHplcAUYY3RsMDAkQm9keUFCQyRpbmRpY2VzbWtwBRljdGwwMCRCb2R5QUJDJGluZGljZXNzZWNwBRFjdGwwMCRCb2R5QUJDJG1scAUSY3RsMDAkQm9keUFCQyRtbGVwBRNjdGwwMCRCb2R5QUJDJG9ibDJwBRJjdGwwMCRCb2R5QUJDJG9ibHAFEmN0bDAwJEJvZHlBQkMkZmNwcAUSY3RsMDAkQm9keUFCQyRzcmRwBRRjdGwwMCRCb2R5QUJDJHNyZGxvcAUUY3RsMDAkQm9keUFCQyR0cmFja3AFFmN0bDAwJEJvZHlBQkMkd2FycmFudHMFFWN0bDAwJEJvZHlBQkMkY2JQbGFjZbr%2F%2Bw4wbaN8kpVzU3GLpnWNxZ9M&&__EVENTVALIDATION=%2FwEdAC5Aj57FihrreLL%2BGrK0Nj5d2AGy%2BFRpYOz7XDkkbfjubp9UXI7RwI%2BukRHnd%2BAlDZ7yE4oeSMQcqAToKX4%2BVY%2FoKwHPZ3LL3fdWqV0S%2FvWmetYHl%2BXtIMfr4sJ5HoKPeEGaXWKkENsUVjCs33ftb%2Bk6Vh68XGlO5A7hLzsl2zmozVHKtnVHMqNjuSl%2FVTLUSxGOrSXMajdQMItHxDOD4gI5oZA%2FrQy55rsm3Yy%2BuTl0%2FnRrfHed0TzZAp%2F%2By2dFmxusO8axFlSjvdrqSAJF9oAESNvpV6G124LKs01uIQT%2BzPLtwgDb4ZnV8AzgWlnJDQlBhudEBAhKHZIsMbDqQKObxt6eBSEoHlSQ0h6eQsjG3FU1EY8C1%2F0GGZDF0VWO2oYTcspg%2FQJIEo7yz4CR037atiIVgEYIzEhkb9KlYZmye7%2FnJk8Wqab3FraRKRA4NH4SMkbr0ssfhDNp48vrt1aToLK%2FxuYkxeDUJ2jyEChuCcZNAzb5On4rHEP9HywghYEYPSSOqNVqe2KgYnr%2BNJRx9At%2BumCzYS2uPoXq%2FdVJByyB7RsbMXUq2lzc8dt95PO%2BkCFfVa1MgDkVN3T3jY%2FKQIBPK12FXYyMGO0%2F5KPgqmM77ItwinSXDlQBQcTHG5JyTAMHmjunt2bP%2Fj%2FgPFOe%2F%2Baf%2FiQDbqNtvcnRfkH2ohjdh6dUQ5m%2B2VTNYWkGxattRg9EKKE5vey1FPRlCqZyDgvrv9lvrlshrlzdNlGwotAGLfLMcs2vyw1rETiNehTmes8eJTYwExj4p4PV%2FEN9ErsbOH1TVXp4%2FWwcDTLXfdl6GdH4ABcdFWEaYFLm8enTJ%2Fw5%2Bt22ztgamirKfEngNFYdXyAUuxt6%2B0jbZc5NwtAP%2BEZsNvl6OsZ5B1J7HBy%2BRgs4xm0Y8WrzA6574vdooZmrulbiQC7mbx9vlyLTOPZK8VW20G8fq75mHTRTGK8a6n1yqBv3gW0GFevOJ3B1rDSAtPbMcXmkpJIMPfiMxmgQJi8%3D&&ctl00%24txtAutoComplete=&&ctl00%24BodyABC%24srdp=on&&ctl00%24BodyABC%24Button1=T%C3%A9l%C3%A9charger
--


curl  -d @payload.txt 'http://www.abcbourse.com/download/libelles.aspx' > /tmp/libelles.csv

*/

load data infile '/tmp/libelles.csv' into table stocks fields terminated by ';' lines terminated by '\r\n' ignore 1 lines (isin,name,@symbol) set symbol = concat(@symbol, '.PA');

delete from selection where idf_selection_name = 1;

insert into selection(idf_selection_name,ticker) select 1, symbol from stocks;


