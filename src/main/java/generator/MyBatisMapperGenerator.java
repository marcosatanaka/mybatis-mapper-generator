package generator;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MyBatisMapperGenerator {

    private static final Class<YOUR_CLASS_HERE> CLAZZ = YOUR_CLASS_HERE.class;

    private static final String NOT_IDENTIFIED = "#PROPERTY_NOT_IDENTIFIED#";
    private static final String LIST_ELEMENT_TYPE = "#ListElementType#";
    private static final String SUBSELECT_NAME = "find%s";

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String TAG_RESULT_MAP_OPEN = "<resultMap id=\"resultMap%s\" type=\"%s\">";
    private static final String TAG_RESULT_MAP_CLOSE = "</resultMap>";
    private static final String TAG_ID = "<id property=\"%s\" column=\"%s\" />";
    private static final String TAG_RESULT = "<result property=\"%s\" column=\"%s\" />";
    private static final String TAG_ASSOCIATION = "<association property=\"%s\" column=\"%s\"" + NEW_LINE + "javaType=\"%s\" select=\"%s\" >" + NEW_LINE + "</association>";
    private static final String TAG_SELECT = "<select id=\"%s\" resultMap=\"resultMap%s\">" + NEW_LINE + "</select>";
    private static final String TAG_COLLECTION = "<collection property=\"%s\" javaType=\"ArrayList\" ofType=\"" + LIST_ELEMENT_TYPE + "\" select=\"findAll%s\" column=\"id\"/>";

    private static final List<String> SUBSELECTS = new ArrayList<String>();

    public static void main(String[] args) {
        System.out.println(String.format(TAG_RESULT_MAP_OPEN, CLAZZ.getSimpleName(), CLAZZ.getSimpleName()));

        Field[] classFields = CLAZZ.getDeclaredFields();

        List<Field> resultOrIDFields = new ArrayList<Field>();
        List<Field> associationFields = new ArrayList<Field>();
        List<Field> collectionFields = new ArrayList<Field>();

        for (Field field : classFields) {
            if (field.getType().equals(String.class)
                    || field.getType().equals(BigDecimal.class)
                    || field.getType().equals(Integer.class) || field.getType().equals(Integer.TYPE)
                    || field.getType().equals(Date.class) || field.getType().equals(Double.class)
                    || field.getType().equals(Long.class) || field.getType().equals(Long.TYPE)
                    || field.getType().equals(Boolean.class) || field.getType().equals(Boolean.TYPE)
                    || field.getType().isEnum()) {
                resultOrIDFields.add(field);
            } else if (field.getType().equals(List.class) || field.getType().equals(Set.class)) {
                collectionFields.add(field);
            } else {
                associationFields.add(field);
            }
        }

        createTagResultAndId(resultOrIDFields);
        createTagAssociation(associationFields);
        createTagCollection(collectionFields);

        System.out.println(TAG_RESULT_MAP_CLOSE);

        createSubselects(SUBSELECTS);
    }

    private static void createTagResultAndId(List<Field> resultOrIDFields) {
        for (Field field : resultOrIDFields) {
            if (idAnnotation(field.getDeclaredAnnotations())) {
                System.out.println(String.format(TAG_ID, field.getName(), getColumnName(field.getDeclaredAnnotations())));
            } else if (!isTransient(field.getDeclaredAnnotations()) && field.getDeclaredAnnotations().length > 0) {
                System.out.println(String.format(TAG_RESULT, field.getName(), getColumnName(field.getDeclaredAnnotations())));
            }
        }
    }

    private static void createTagAssociation(List<Field> associationFields) {
        for (Field field : associationFields) {
            if (!isTransient(field.getDeclaredAnnotations())) {
                String subselectName = String.format(SUBSELECT_NAME, field.getType().getSimpleName());
                System.out.println(String.format(TAG_ASSOCIATION, field.getName(), getColumnName(field.getDeclaredAnnotations()), field.getType().getSimpleName(), subselectName));
                SUBSELECTS.add(String.format(TAG_SELECT, subselectName, field.getType().getSimpleName()));
            }
        }
    }

    private static void createTagCollection(List<Field> collectionFields) {
        for (Field field : collectionFields) {
            System.out.println(String.format(TAG_COLLECTION, field.getName(), field.getName()));
        }
    }

    private static void createSubselects(List<String> subSelects) {
        for (String select : subSelects) {
            System.out.println(NEW_LINE + select);
        }
    }

    private static Boolean isTransient(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Transient) {
                return true;
            }
        }
        return false;
    }

    private static boolean idAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Id) {
                return true;
            }
        }
        return false;
    }

    private static String getColumnName(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Column) {
                return ((Column) annotation).name();
            }
            if (annotation instanceof JoinColumn) {
                return ((JoinColumn) annotation).name();
            }
        }

        return NOT_IDENTIFIED;
    }

}