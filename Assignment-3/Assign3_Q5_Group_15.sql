drop view required;
create view required as select w1.author_name as author1, w1.paper_id, w2.author_name as author2 
from writtenby as w1 inner join writtenby as w2 on w1.paper_id = w2.paper_id and w1.author_name < w2.author_name;
select author1, author2, count(*) cnt from required group by author1, author2 having count(*) > 1; 