CREATE VIEW onlyless(less,greater) AS (
    SELECT
        citations.main_paper_id, citations.ref_paper_id
    FROM
        citations
    WHERE
        citations.main_paper_id < citations.ref_paper_id and citations.ref_paper_id is not null 
UNION ALL
    SELECT DISTINCT
        citations.ref_paper_id, citations.main_paper_id
    FROM
        citations
    WHERE
        citations.main_paper_id > citations.ref_paper_id and citations.ref_paper_id is not null 
);

CREATE VIEW XYZ AS SELECT o1.less as X, o1.greater as Y, o2.greater as Z FROM onlyless o1 INNER JOIN onlyless o2 ON o1.greater = o2.less 
INNER JOIN onlyless o3 ON (o2.greater = o3.greater AND o3.less = o1.less);

CREATE VIEW triangle AS SELECT a1.author_name as x, a2.author_name as y,a3.author_name as z
from writtenby as a1, writtenby as a2, writtenby as a3, XYZ where a1.paper_id = XYZ.X and a2.paper_id = XYZ.Y and a3.paper_id = XYZ.Z
and a1.author_name <> a2.author_name and a2.author_name <> a3.author_name and a1.author_name <> a3.author_name
and a1.author_name <> '' and a2.author_name <> '' and a3.author_name <> '';

select x,y,z,count(*) from (SELECT x,y,z from triangle where x< y and y<z union all 
select x,z,y from triangle where x<z and z<y union all 
select y,x,z from triangle where y<x and x<z union all 
select y,z,x from triangle where y<z and z<x union all 
select z,x,y from triangle where z<x and x<y union all 
select z,y,x from triangle where z<y and y<x) as u group by x,y,z ;