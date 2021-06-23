# incompatibility-smell

本项目用于发现Kubernetes版本的不兼容问题，主要包括以下几个概念。

- Features: 特征，在Kubernetes主要是指Kind，如Pod, Deployment
  - removed: 高版本Kubernetes相对于低版本Kubernetes不支持的Kind
  - added:   高版本Kubernetes相对于低版本Kubernetes新支持的Kind
- Fields： 属性，在Kubernetes主要指Pod、Deployment等JSON构成
  - removed: 高版本Kubernetes相对于低版本Kubernetes的Kind，不支持的属性
  - added:   高版本Kubernetes相对于低版本Kubernetes的Kind，新支持的属性
  

# 使用示例示例

## 使用本工具
```
git clone https://github.com/bocloud-plus/incompatibility-smell.git
mvn clean install -DskipTests
java -jar target/incompatibility-smell-0.1-jar-with-dependencies.jar --thisVersion 1.18 --preVersion 1.16
```

## 输出结果
```
{
	"ThisVersion": "1.18",
	"PreviousVersion": "1.16",
	"features": {
		"added": [{
			"apiVersion": "networking.k8s.io/v1beta1",
			"kind": "IngressClass"
		}, {
			"apiVersion": "storage.k8s.io/v1",
			"kind": "CSIDriver"
		}, {
			"apiVersion": "storage.k8s.io/v1",
			"kind": "CSINode"
		}],
		"removed": [{
			"apiVersion": "extensions/v1beta1",
			"kind": "ReplicationControllerDummy"
		}]
	},
	"fields": {
		"removed": {
			"DaemonSet": {
				"spec": {
					"updateStrategy": {
						"type": "荥ơ'禧ǵŊ)TiD¢ƿ媴h5"
					}
				}
			}
		},
		"added": {
			"DaemonSet": {
				"spec": {
					"template": {
						"spec": {
							"securityContext": {
								"fsGroupChangePolicy": "Ĕ\\ɢX鰨松/Ȁĵ鴁ĩȲǸ|蕎"
							}
						}
					},
					"updateStrategy": {
						"rollingUpdate": {
							"maxUnavailable": 2
						}
					}
				}
			},
			"Deployment": {
				"spec": {
					"template": {
						"spec": {
							"securityContext": {
								"fsGroupChangePolicy": "Ņ#耗Ǚ("
							}
						}
					},
					"strategy": {
						"rollingUpdate": {
							"maxUnavailable": 2,
							"maxSurge": 3
						}
					}
				}
			},
			"ReplicaSet": {
				"spec": {
					"template": {
						"spec": {
							"securityContext": {
								"fsGroupChangePolicy": "蹔ŧ"
							}
						}
					}
				}
			},
			"StatefulSet": {
				"spec": {
					"template": {
						"spec": {
							"securityContext": {
								"fsGroupChangePolicy": "Ņ#耗Ǚ("
							}
						}
					}
				}
			},
			"HorizontalPodAutoscaler": {
				"spec": {
					"behavior": {
						"scaleUp": {
							"stabilizationWindowSeconds": 1761963371,
							"selectPolicy": "0矀Kʝ瘴I\\p[ħsĨɆâĺɗŹ倗S",
							"policies": [{
								"type": "嶗U",
								"value": -1285424066,
								"periodSeconds": -686523310
							}]
						},
						"scaleDown": {
							"stabilizationWindowSeconds": 1206365825,
							"selectPolicy": "/ɸɎ R§耶FfBls3!",
							"policies": [{
								"type": "ɾģ毋Ó6ǳ娝嘚",
								"value": 627713162,
								"periodSeconds": 1255312175
							}]
						}
					}
				}
			},
			"Job": {
				"spec": {
					"template": {
						"spec": {
							"securityContext": {
								"fsGroupChangePolicy": "展}硐庰%皧V垾现葢ŵ橨鬶l獕;跣"
							}
						}
					}
				}
			},
			"CronJob": {
				"spec": {
					"jobTemplate": {
						"spec": {
							"template": {
								"spec": {
									"securityContext": {
										"fsGroupChangePolicy": "蹔ŧ"
									}
								}
							}
						}
					}
				}
			},
			"JobTemplate": {
				"template": {
					"spec": {
						"template": {
							"spec": {
								"securityContext": {
									"fsGroupChangePolicy": "ɱďW賁Ě"
								}
							}
						}
					}
				}
			},
			"CertificateSigningRequest": {
				"spec": {
					"signerName": "19"
				}
			},
			"Lease": {
				"spec": {
					"acquireTime": "1970-01-01T00:00:02.000000Z",
					"renewTime": "1970-01-01T00:00:03.000000Z"
				}
			},
			"ConfigMap": {
				"immutable": false
			},
			"Pod": {
				"spec": {
					"securityContext": {
						"fsGroupChangePolicy": "勅跦Opwǩ曬逴褜1Ø"
					}
				}
			},
			"PodLogOptions": {
				"insecureSkipTLSVerifyBackend": true
			},
			"PodTemplate": {
				"template": {
					"spec": {
						"securityContext": {
							"fsGroupChangePolicy": "勅跦Opwǩ曬逴褜1Ø"
						}
					}
				}
			},
			"ReplicationController": {
				"spec": {
					"template": {
						"spec": {
							"securityContext": {
								"fsGroupChangePolicy": ""
							}
						}
					}
				}
			},
			"Secret": {
				"immutable": false
			},
			"Service": {
				"spec": {
					"topologyKeys": ["29"]
				}
			},
			"Ingress": {
				"spec": {
					"ingressClassName": "19",
					"backend": {
						"resource": {
							"apiGroup": "22",
							"kind": "23",
							"name": "24"
						}
					}
				}
			},
			"PodDisruptionBudget": {
				"spec": {
					"minAvailable": 2,
					"maxUnavailable": 3
				}
			}
		}
	}
}
```
