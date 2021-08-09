package br.com.zupacademy.rayllanderson.core.validators

import br.com.zupacademy.rayllanderson.pix.register.RegisterPixKeyRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [PixKeyValidador::class])
annotation class ValidPixKey(
    val message: String = "Chave pix está inválida. Exemplos de valores válidos:[CPF = 12345678901],[PHONE = +5585988714077],[RANDOM = Não preencher o valor da chave]",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class PixKeyValidador: ConstraintValidator<ValidPixKey, RegisterPixKeyRequest> {
    override fun isValid(
        request: RegisterPixKeyRequest?,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: ConstraintValidatorContext,
    ): Boolean {
        if(request?.keyType == null) return false

        return request.keyType.validate(request.key)
    }
}
