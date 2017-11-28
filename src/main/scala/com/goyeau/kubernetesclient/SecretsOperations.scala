package com.goyeau.kubernetesclient

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import io.circe._
import io.k8s.api.core.v1.{Secret, SecretList}

private[kubernetesclient] case class SecretsOperations(protected val config: KubeConfig)(
  implicit protected val system: ActorSystem,
  protected val resourceDecoder: Decoder[SecretList],
  encoder: Encoder[Secret],
  decoder: Decoder[Secret]
) extends Listable[SecretList] {
  protected val resourceUri = s"${config.server}/api/v1/secrets"

  def namespace(namespace: String) = NamespacedSecretsOperations(config, namespace)
}

private[kubernetesclient] case class NamespacedSecretsOperations(protected val config: KubeConfig,
                                                                 protected val namespace: String)(
  implicit protected val system: ActorSystem,
  protected val resourceEncoder: Encoder[Secret],
  decoder: Decoder[Secret],
  protected val resourceDecoder: Decoder[SecretList]
) extends Creatable[Secret]
    with CreateOrUpdatable[Secret]
    with Listable[SecretList]
    with GroupDeletable {
  protected val resourceUri = s"${config.server}/api/v1/namespaces/$namespace/secrets"

  def apply(configMapName: String) = SecretOperations(config, s"$resourceUri/$configMapName")
}

private[kubernetesclient] case class SecretOperations(protected val config: KubeConfig, protected val resourceUri: Uri)(
  implicit protected val system: ActorSystem,
  protected val resourceEncoder: Encoder[Secret],
  protected val resourceDecoder: Decoder[Secret]
) extends Gettable[Secret]
    with Replaceable[Secret]
    with Deletable
