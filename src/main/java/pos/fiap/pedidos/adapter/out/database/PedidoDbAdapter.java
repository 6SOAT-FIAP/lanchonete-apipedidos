package pos.fiap.pedidos.adapter.out.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pos.fiap.pedidos.adapter.out.database.entities.mapper.PedidoEntityMapper;
import pos.fiap.pedidos.adapter.out.database.repository.PedidoRepository;
import pos.fiap.pedidos.adapter.out.exception.PedidoNotFoundException;
import pos.fiap.pedidos.domain.enums.StatusPedidoEnum;
import pos.fiap.pedidos.domain.model.entity.Pedido;
import pos.fiap.pedidos.port.PedidoDbAdapterPort;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static pos.fiap.pedidos.utils.Constantes.FIM;
import static pos.fiap.pedidos.utils.Constantes.INICIO;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoDbAdapter implements PedidoDbAdapterPort {
    private static final String SERVICE_NAME = "PedidoDbAdapter";
    private static final String CADASTRAR_PEDIDO_METHOD_NAME = "cadastrarPedido";
    private static final String BUSCAR_PEDIDOS_METHOD_NAME = "buscarPedidos";
    private static final String OBTER_PEDIDO_POR_ID_METHOD_NAME = "obterPedidoPorId";
    private static final String STRING_LOG_FORMAT = "%s_%s_%s {}";
    private final PedidoRepository pedidoRepository;
    private final PedidoEntityMapper pedidoEntityMapper;

    @Override
    public Pedido cadastrarPedido(Pedido pedido) {
        log.info(String.format(STRING_LOG_FORMAT, SERVICE_NAME, CADASTRAR_PEDIDO_METHOD_NAME, INICIO), pedido);

        var pedidoEntity = pedidoEntityMapper.toEntity(pedido);

        var entity = pedidoRepository.save(pedidoEntity);
        pedido.setNumeroPedido(entity.getNumeroPedido());
        pedido.setMensagemPedido("Pedido Realizado com sucesso");

        log.info(String.format(STRING_LOG_FORMAT, SERVICE_NAME, CADASTRAR_PEDIDO_METHOD_NAME, FIM), pedido);
        return pedido;
    }

    @Override
    public List<Pedido> buscarPedidos() {
        log.info(String.format(STRING_LOG_FORMAT, SERVICE_NAME, BUSCAR_PEDIDOS_METHOD_NAME, INICIO), "pedidos");

        var entity = new ArrayList<>(pedidoRepository.findAll());

        entity.removeIf(pedido ->
                isNull(pedido.getStatusPedido()) || pedido.getStatusPedido() == StatusPedidoEnum.FINALIZADO);

        var pedidoList = pedidoEntityMapper.toListPedido(entity);

        log.info(String.format(STRING_LOG_FORMAT, SERVICE_NAME, BUSCAR_PEDIDOS_METHOD_NAME, FIM), pedidoList);
        return pedidoList;
    }

    @Override
    public Pedido obterPedidoPorId(String id) {
        log.info(String.format(STRING_LOG_FORMAT, SERVICE_NAME, OBTER_PEDIDO_POR_ID_METHOD_NAME, INICIO), id);

        var entity = pedidoRepository.findById(id);

        if (entity.isEmpty()) {
            log.info(String.format(STRING_LOG_FORMAT, SERVICE_NAME, OBTER_PEDIDO_POR_ID_METHOD_NAME, FIM),
                    "Não foi encontrado pedido para o id {}", id);
            throw new PedidoNotFoundException(String.format("Não foi encontrado pedido para o id %s", id));
        }

        var pedido = pedidoEntityMapper.toPedido(entity.get());
        log.info(String.format(STRING_LOG_FORMAT, SERVICE_NAME, OBTER_PEDIDO_POR_ID_METHOD_NAME, FIM), pedido);
        return pedido;
    }
}
