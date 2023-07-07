select citations.ref_paper_id, count(citations.ref_paper_id) as cnt 
from citations group by citations.ref_paper_id order by cnt desc limit 20;