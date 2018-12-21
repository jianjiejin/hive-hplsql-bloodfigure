MERGE INTO acid__cinocsno__TMP_NDS_KER_BCFMCMBI_SPECIAL  A
               USING  ( SELECT
                           A.CINOCSNO
                          ,CASE
                             WHEN A.MXASPTSN =  B.MXASPTSN AND A.MXPSPTSN =  B.MXPSPTSN THEN 'O'
                             WHEN A.MXASPTSN <> B.MXASPTSN AND A.MXPSPTSN <> B.MXPSPTSN THEN 'U3'
                             WHEN A.MXASPTSN =  B.MXASPTSN AND A.MXPSPTSN <> B.MXPSPTSN THEN 'U1'
                             WHEN A.MXASPTSN <> B.MXASPTSN AND A.MXPSPTSN =  B.MXPSPTSN THEN 'U2'
                           END AS CS
                       FROM NDS_KER_BCFMCMBI_SPECIAL B,acid__cinocsno__TMP_NDS_KER_BCFMCMBI_SPECIAL  A
                             WHERE  A.CINOCSNO = B.CINOCSNO
               ) B
            ON  A.CINOCSNO = B.CINOCSNO
      WHEN  MATCHED  THEN   UPDATE
            SET   ETL_FLAG=B.CS ;