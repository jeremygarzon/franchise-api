INSERT INTO franchise (id, name)
SELECT 1, 'Acme Franchise'
WHERE NOT EXISTS (SELECT 1 FROM franchise WHERE id = 1);

INSERT INTO branch (id, name, franchise_id)
SELECT 1, 'Downtown Branch', 1
WHERE NOT EXISTS (SELECT 1 FROM branch WHERE id = 1);

INSERT INTO branch (id, name, franchise_id)
SELECT 2, 'Uptown Branch', 1
WHERE NOT EXISTS (SELECT 1 FROM branch WHERE id = 2);

INSERT INTO product (id, name, stock, branch_id)
SELECT 1, 'Widget', 25, 1
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 1);

INSERT INTO product (id, name, stock, branch_id)
SELECT 2, 'Gadget', 40, 1
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 2);

INSERT INTO product (id, name, stock, branch_id)
SELECT 3, 'Gizmo', 10, 2
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 3);

INSERT INTO product (id, name, stock, branch_id)
SELECT 4, 'Sprocket', 55, 2
WHERE NOT EXISTS (SELECT 1 FROM product WHERE id = 4);
