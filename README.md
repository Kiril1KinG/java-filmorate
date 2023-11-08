# java-filmorate
Template repository for Filmorate project.

![](../../Desktop/ER-диаграмма.png)
### Ссылочка на ER-диаграмму https://dbdesigner.page.link/86uxnNnu6S44UeRp6
### 1. Пример получения ТОП 10 фильмов:
#### SELECT title FROM Film WHERE film_id IN (SELECT film_id, COUNT(user_id) AS likes FROM Like GROUP BY film_id ORDER BY likes DESC LIMIT 10);
### 2. Пример получения общих друзей. Допустим юзер с Id = 2, хочет получить общих друзей с юзером c id = 4:
#### SELECT name FROM user 
#### WHERE user_id IN ((SELECT friend_id FROM frendship WHERE frendship_status = true AND user_id = 2) AS u1
#### INNER JOIN
#### (SELECT friend_id FROM frendship WHERE frendship_status = true AND user_id = 4) AS u2 ON u1.friend_id = u2.frined_id))
