package net.openrally.restaurant.core.request.body;

import java.lang.reflect.Field;

import net.openrally.restaurant.core.annotation.ParameterRequired;
import net.openrally.restaurant.core.annotation.ParameterValuePositive;
import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.exception.InternalServerErrorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseRequestBody {

	private transient final Logger logger = LoggerFactory.getLogger(this
			.getClass());

	public void validate() throws BadRequestException {
		Field[] fields = this.getClass().getDeclaredFields();

		for (Field field : fields) {
			field.setAccessible(true);

			try {

				if (null != field.getAnnotation(ParameterRequired.class)) {

					if (field.getType() == String.class) {
						String fieldValue = (String) field.get(this);

						if (StringUtils.isBlank(fieldValue)) {
							throw new BadRequestException("Parameter "
									+ field.getName() + " missing or blank");
						}
					} else if (field.getType() == Long.class || field.getType() == Long.class) {
						Long fieldValue = (Long) field.get(this);

						if (fieldValue == null) {
							throw new BadRequestException("Parameter "
									+ field.getName() + " missing or blank");
						}
					} 
					else if (field.getType() == Integer.class || field.getType() == int.class) {
						Integer fieldValue = (Integer) field.get(this);

						if (fieldValue == null) {
							throw new BadRequestException("Parameter "
									+ field.getName() + " missing or blank");
						}
					}
					else if (field.getType() == Double.class || field.getType() == double.class){
						Double fieldValue = (Double) field.get(this);

						if (fieldValue == null) {
							throw new BadRequestException("Parameter "
									+ field.getName() + " missing or blank");
						}
					}
					else{
						logger.error("Unkown request body validation policy. Class " + this.getClass().getSimpleName() + ", field name " + field.getName() + ", field type " + field.getType().getSimpleName() + ", annotation ParameterRequired");
						throw new InternalServerErrorException("Error while validating request");
					}

				}
				
				if (null != field.getAnnotation(ParameterValuePositive.class)) {

					if (field.getType() == Long.class || field.getType() == Long.class) {
						Long fieldValue = (Long) field.get(this);

						if (null != fieldValue && fieldValue <= 0 ) {
							throw new BadRequestException("Parameter "
									+ field.getName() + " negative or zero");
						}
					}
					else if (field.getType() == Integer.class || field.getType() == int.class) {
						Integer fieldValue = (Integer) field.get(this);

						if (fieldValue <= 0) {
							throw new BadRequestException("Parameter "
									+ field.getName() + " negative or zero");
						}
					}
					else{
						logger.error("Unkown request body validation policy. Class " + this.getClass().getSimpleName() + ", field name " + field.getName() + ", field type " + field.getType().getSimpleName() + ", annotation ParameterValuePositive");
						throw new InternalServerErrorException("Error while validating request");
					}

				}

			} catch (IllegalArgumentException e) {
				logger.error(
						"IllegalArgumentException while validating request body of type "
								+ this.getClass().getSimpleName()
								+ " in field " + field.getName(), e);
				throw new InternalServerErrorException("Error while validating request");
			} catch (IllegalAccessException e) {
				logger.error(
						"IllegalAccessException while validating request body of type "
								+ this.getClass().getSimpleName()
								+ " in field " + field.getName(), e);
				throw new InternalServerErrorException("Error while validating request");
			}
		}

	}
}
